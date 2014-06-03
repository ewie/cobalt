/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.utils.ProductSet;

/**
 * A graph extender takes a not fully satisfied graph and extends its by one level at a time.
 *
 * @author Erik Wienhold
 */
public class GraphExtender {

  private final PrecursorActionProvider precursorActionProvider;

  private final PropertyProvisionProvider propertyProvisionProvider;

  private final CyclicDependencyDetector cyclicDependencyDetector;

  /**
   * @param precursorActionProvider   a provider of precursor actions
   * @param propertyProvisionProvider a provider of property provisions
   * @param cyclicDependencyDetector  a detector of cyclic dependencies between actions
   */
  public GraphExtender(final PrecursorActionProvider precursorActionProvider,
                       final PropertyProvisionProvider propertyProvisionProvider,
                       final CyclicDependencyDetector cyclicDependencyDetector) {
    this.precursorActionProvider = precursorActionProvider;
    this.propertyProvisionProvider = propertyProvisionProvider;
    this.cyclicDependencyDetector = cyclicDependencyDetector;
  }

  /**
   * Extend a graph by one level.
   *
   * @param graph a planning graph to extend
   *
   * @return an extended planning graph
   *
   * @throws PlanningException when the last level cannot be satisfied
   */
  public Graph extendGraph(final Graph graph) throws PlanningException {
    final Set<Action> as = selectUnsatisfiedRequiredActions(graph);

    if (as.isEmpty()) {
      throw new IllegalArgumentException("cannot extend satisfied graph");
    }

    final Collection<Candidate> cs = findCandidates(as, graph);
    final Set<ActionProvision> aps = createActionProvisions(cs,
        indexPropertyProvisions(
            provideCompatibleProperties(
                collectRequiredProperties(cs))),
        graph);

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
      final Set<Action> precursors = providePrecursorActions(ra);
      if (precursors.isEmpty()) {
        if (!ra.requiresPrecursor()) {
          candidates.add(new Candidate(ra));
        }
      } else {
        for (final Action pa : precursors) {
          if (!createsCyclicDependency(pa, ra, graph)) {
            candidates.add(new Candidate(ra, pa));
          }
        }
      }
    }
    return candidates;
  }

  /**
   * Create action provisions for each candidate and combination of applicable property provisions.
   *
   * @param candidates a collection of candidates
   * @param index      an index of property provisions
   *
   * @return a set of action provisions
   */
  private Set<ActionProvision> createActionProvisions(final Collection<Candidate> candidates,
                                                      final Index index, final Graph graph) {
    final Set<ActionProvision> aps = new HashSet<>();

    for (final Candidate c : candidates) {
      if (c.requiresProperties()) {
        aps.add(createActionProvision(c));
      } else {
        for (final Set<PropertyProvision> combination : index.getCombinations(c.requiredProperties)) {
          if (!createsCyclicDependency(combination, c.request, graph)) {
            aps.add(createActionProvision(c, combination));
          }
        }
      }
    }

    return aps;
  }

  /**
   * Test if any providing action from a set of property provisions causes a cyclic dependency via dependent action,
   * e.g. a candidate's requested action.
   *
   * @param provisions a set of property provisions
   * @param dependent  a dependent action
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
