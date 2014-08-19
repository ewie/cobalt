/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.planner.GraphExtender;
import vsr.cobalt.planner.PlanningException;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.providers.PrecursorActionProvider;
import vsr.cobalt.planner.providers.PropertyProvisionProvider;
import vsr.cobalt.utils.ProductSet;

/**
 * The default graph extender extends a graph by satisfying its required actions with precursor actions and actions
 * publishing additionally required properties.
 *
 * @author Erik Wienhold
 */
public class DefaultGraphExtender implements GraphExtender {

  private final PrecursorActionProvider precursorActionProvider;

  private final PropertyProvisionProvider propertyProvisionProvider;

  private final CyclicDependencyDetector cyclicDependencyDetector;

  /**
   * @param precursorActionProvider   a provider of precursor actions
   * @param propertyProvisionProvider a provider of property provisions
   * @param cyclicDependencyDetector  a detector of cyclic dependencies between actions
   */
  public DefaultGraphExtender(final PrecursorActionProvider precursorActionProvider,
                              final PropertyProvisionProvider propertyProvisionProvider,
                              final CyclicDependencyDetector cyclicDependencyDetector) {
    this.precursorActionProvider = precursorActionProvider;
    this.propertyProvisionProvider = propertyProvisionProvider;
    this.cyclicDependencyDetector = cyclicDependencyDetector;
  }

  @Override
  public Graph extendGraph(final Graph graph) throws PlanningException {
    final Set<Action> as = selectUnsatisfiedRequiredActions(graph);

    if (as.isEmpty()) {
      throw new IllegalArgumentException("cannot extend satisfied graph");
    }

    final Collection<Candidate> cs = findCandidates(as, graph);
    final Set<ActionProvision> aps = createActionProvisions(cs, graph,
        indexPropertyProvisions(
            provideCompatibleProperties(
                collectRequiredProperties(cs))));

    if (aps.isEmpty()) {
      throw new PlanningException("cannot satisfy any action");
    }

    return graph.extendWith(new ExtensionLevel(aps));
  }

  /**
   * Find candidates for a set of required actions.
   *
   * @param requiredActions a set of required actions
   *
   * @return a collection of candidates for any required action
   */
  private Collection<Candidate> findCandidates(final Set<Action> requiredActions, final Graph graph) {
    final Collection<Candidate> candidates = new ArrayList<>();
    for (final Action ra : requiredActions) {
      final Set<Action> precursors = filterCyclicDependentActions(providePrecursorActions(ra), ra, graph);
      if (precursors.isEmpty()) {
        if (!ra.requiresPrecursor()) {
          candidates.add(new Candidate(ra));
        }
      } else {
        for (final Action pa : precursors) {
          candidates.add(new Candidate(ra, pa));
        }
      }
    }
    return candidates;
  }

  /**
   * Create action provisions for each candidate and combination of applicable property provisions.
   *
   * @param candidates a collection of candidates
   * @param graph      the graph to create action provisions for
   * @param index      an index of property provisions
   *
   * @return a set of action provisions
   */
  private Set<ActionProvision> createActionProvisions(final Collection<Candidate> candidates,
                                                      final Graph graph, final Index index) {
    final Set<ActionProvision> aps = new HashSet<>();

    for (final Candidate c : candidates) {
      if (c.requiresProperties()) {
        aps.add(createActionProvision(c));
      } else {
        for (final Set<PropertyProvision> combination : index.getCombinations(c.requiredProperties)) {
          if (canCreateActionProvision(c, combination, graph)) {
            aps.add(createActionProvision(c, combination));
          }
        }
      }
    }

    return aps;
  }

  /**
   * Filter all supporting actions which create a cyclic dependency via some dependent action.
   *
   * @param supports  a set of supporting actions
   * @param dependent a dependent action
   * @param graph     a graph containing the dependent action
   *
   * @return a set of supporting actions not creating a cyclic dependency via the dependent action
   */
  private Set<Action> filterCyclicDependentActions(final Set<Action> supports, final Action dependent,
                                                   final Graph graph) {
    final Set<Action> filtered = new HashSet<>();
    for (final Action support : supports) {
      if (!createsCyclicDependency(support, dependent, graph)) {
        filtered.add(support);
      }
    }
    return filtered;
  }

  /**
   * Check if we can create an action provision.
   *
   * @param candidate  an action provision candidate
   * @param provisions a set of property provisions for the candidate
   * @param graph      a graph for which the action provision should be created
   *
   * @return true when an action provision can be created, false otherwise
   */
  private boolean canCreateActionProvision(final Candidate candidate, final Set<PropertyProvision> provisions,
                                           final Graph graph) {
    // We can create an action provision when it does not create any cyclic dependencies.
    // Furthermore we will not allow an action provision when its providing actions are not disjoint. This way we can
    // avoid having providing actions whose functionality (interactions, published properties, ...) is provided by
    // another action in the same action provision.
    // XXX This assumes that the property provision provider, when returning non-disjoint providing actions, will also
    // return disjoint providing actions. Otherwise we would have to use the non-disjoint variant to extend the graph
    // with some applicable action provisions.

    return haveDisjointProvidingActions(provisions)
        && !createsCyclicDependency(provisions, candidate.request, graph);
  }

  /**
   * Test if any providing action from a set of property provisions causes a cyclic dependency via dependent action,
   * e.g. a candidate's requested action.
   *
   * @param provisions a set of property provisions
   * @param dependent  a dependent action
   * @param graph      a graph containing the dependent action
   *
   * @return true when any providing action causes a cyclic dependency, false otherwise
   */
  private boolean createsCyclicDependency(final Set<PropertyProvision> provisions, final Action dependent,
                                          final Graph graph) {
    for (final PropertyProvision pp : provisions) {
      if (createsCyclicDependency(pp.getProvidingAction(), dependent, graph)) {
        return true;
      }
    }
    return false;
  }

  private Set<Action> providePrecursorActions(final Action action) {
    return precursorActionProvider.getPrecursorActionsFor(action);
  }

  private Set<PropertyProvision> provideCompatibleProperties(final Set<Property> properties) {
    return propertyProvisionProvider.getProvisionsFor(properties);
  }

  private boolean createsCyclicDependency(final Action support, final Action dependent, final Graph graph) {
    return cyclicDependencyDetector.createsCyclicDependencyVia(support, dependent, graph);
  }

  /**
   * Check if the providing actions of a set of property provisions are disjoint, i.e. no providing action represents
   * any other providing action.
   *
   * @param provisions a set of property provisions
   *
   * @return true when providing actions are disjoint, false otherwise
   */
  private static boolean haveDisjointProvidingActions(final Set<PropertyProvision> provisions) {
    for (final PropertyProvision pp1 : provisions) {
      for (final PropertyProvision pp2 : provisions) {
        final Action a1 = pp1.getProvidingAction();
        final Action a2 = pp2.getProvidingAction();

        // Because a nested loop pairs an action with itself and an action represents itself, we have to make sure to
        // ignore those pairings.
        if (!a1.equals(a2) && a1.represents(a2)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Collection the required properties among all candidates.
   *
   * @param candidates a collection of candidates
   *
   * @return a set of required properties
   */
  private static Set<Property> collectRequiredProperties(final Collection<Candidate> candidates) {
    final Set<Property> properties = new HashSet<>();
    for (final Candidate c : candidates) {
      properties.addAll(c.requiredProperties);
    }
    return properties;
  }

  /**
   * Select all actions required by a graph's last level which are not already satisfied.
   *
   * @param graph a graph
   *
   * @return a set of unsatisfied actions
   */
  private static Set<Action> selectUnsatisfiedRequiredActions(final Graph graph) {
    final Set<Action> as = new HashSet<>();
    for (final Action a : graph.getLastLevel().getRequiredActions()) {
      if (!a.isEnabled()) {
        as.add(a);
      }
    }
    return as;
  }

  private static ActionProvision createActionProvision(final Candidate candidate) {
    return ActionProvision.createWithPrecursor(candidate.request, candidate.precursor);
  }

  private static ActionProvision createActionProvision(final Candidate candidate,
                                                       final Set<PropertyProvision> propertyProvisions) {
    return ActionProvision.createWithPrecursor(candidate.request, candidate.precursor, propertyProvisions);
  }

  private static Index indexPropertyProvisions(final Set<PropertyProvision> propertyProvisions) {
    return new Index(propertyProvisions);
  }

  /**
   * A potential action provision.
   */
  private static class Candidate {

    /**
     * An action requiring enabling.
     */
    public final Action request;

    /**
     * An optional precursor action for {@link #request}. When absent {@link #request} does not require a precursor.
     */
    public final Action precursor;

    /**
     * The additionally required properties when {@link #request} should be enabled by {@link #precursor}.
     */
    public final Set<Property> requiredProperties;

    /**
     * @param request            a requested action
     * @param precursor          an optional precursor action
     * @param requiredProperties a set of required properties
     */
    private Candidate(final Action request, final Action precursor, final Set<Property> requiredProperties) {
      this.request = request;
      this.precursor = precursor;
      this.requiredProperties = requiredProperties;
    }

    /**
     * Create a candidate using a precursor action.
     *
     * @param request   a requested action
     * @param precursor a precursor action
     */
    public Candidate(final Action request, final Action precursor) {
      this(request, precursor, request.getFilledPropertiesNotSatisfiedByPrecursor(precursor));
    }

    /**
     * Create a candidate not requiring a precursor action.
     *
     * @param request a requested action
     */
    public Candidate(final Action request) {
      this(request, null, request.getPreConditions().getFilledProperties());
    }

    public boolean requiresProperties() {
      return requiredProperties.isEmpty();
    }

  }

  /**
   * Indexes a set of property provisions by their requested properties.
   */
  private static class Index {

    private final SetMultimap<Property, PropertyProvision> index = HashMultimap.create();

    /**
     * @param propertyProvisions a set of property provisions
     */
    public Index(final Set<PropertyProvision> propertyProvisions) {
      for (final PropertyProvision pp : propertyProvisions) {
        index.put(pp.getRequest(), pp);
      }
    }

    /**
     * Get all combination of property provisions having a set of requested properties.
     *
     * @param properties a set of properties
     *
     * @return an iterable of all matching combinations
     */
    public ProductSet<PropertyProvision> getCombinations(final Set<Property> properties) {
      final Set<Set<PropertyProvision>> ppss = new HashSet<>(properties.size());
      for (final Property p : properties) {
        final Set<PropertyProvision> pps = index.get(p);
        // the combinations must comprise all given properties
        if (pps.isEmpty()) {
          return ProductSet.empty();
        }
        ppss.add(pps);
      }
      return new ProductSet<>(ppss);
    }

  }

}
