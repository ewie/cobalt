/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.Task;
import vsr.cobalt.utils.ProductSet;

/**
 * A graph extender extends an unsatisfied graph by one level.
 *
 * @author Erik Wienhold
 */
public class GraphExtender {

  private final Repository repository;

  /**
   * Create a new graph extender.
   *
   * @param repository a repository providing information on how to satisfy certain requirements
   */
  public GraphExtender(final Repository repository) {
    this.repository = repository;
  }

  /**
   * Create an initial planning graph from a given goal.
   *
   * @param goal a goal
   *
   * @return an initial planning graph
   *
   * @throws PlanningException when the goal cannot be fully satisfied
   */
  public Graph createGraph(final Goal goal) throws PlanningException {
    return Graph.create(createInitialLevel(goal));
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
    return graph.extendWith(createExtensionLevel(graph));
  }

  private InitialLevel createInitialLevel(final Goal goal) throws PlanningException {
    final Set<TaskProvision> tps = new HashSet<>();

    for (final Task t : goal.getTasks()) {
      final Set<TaskProvision> tps2 = repository.realizeCompatibleTasks(t);
      if (tps2.isEmpty()) {
        // Every goal task is equally required, so failing to find realizing actions for any single task is a failure.
        throw new PlanningException("cannot realize some goal task");
      } else {
        tps.addAll(tps2);
      }
    }

    return new InitialLevel(tps);
  }

  private ExtensionLevel createExtensionLevel(final Graph graph) throws PlanningException {
    if (isSatisfied(graph)) {
      throw new PlanningException("graph has no unsatisfied actions");
    }

    final Set<Action> as = graph.getLastLevel().getRequiredActions();
    final Set<ActionProvision> aps = new HashSet<>();

    for (final Action a : as) {
      aps.addAll(createActionProvisions(a));
    }

    if (aps.isEmpty()) {
      throw new PlanningException("cannot enable any action required by the graph");
    }

    return new ExtensionLevel(aps);
  }

  private boolean isSatisfied(final Graph graph) {
    for (final Action a : graph.getLastLevel().getRequiredActions()) {
      if (!a.getPreConditions().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private Set<ActionProvision> createActionProvisions(final Action action) {
    final Set<ActionProvision> aps = new HashSet<>();
    final Set<Action> precursors = repository.findPrecursors(action);

    if (precursors.isEmpty()) {
      aps.addAll(createActionProvisionsWithoutPrecursor(action));
    } else {
      for (final Action precursor : precursors) {
        aps.addAll(createActionProvisionsWithPrecursor(action, precursor));
      }
    }

    return aps;
  }

  private Set<ActionProvision> createActionProvisionsWithPrecursor(final Action request, final Action precursor) {
    final Set<Property> ps = request.getFilledPropertiesNotSatisfiedByPrecursor(precursor);

    if (ps.isEmpty()) {
      // The precursor is sufficient enough to enable the action.
      return Sets.newHashSet(ActionProvision.createWithPrecursor(request, precursor));
    } else {
      final ProductSet<PropertyProvision> ppps = findPropertyProvisions(ps);
      if (ppps == null) {
        return Collections.emptySet();
      }
      final Set<ActionProvision> aps = new HashSet<>();
      for (final Set<PropertyProvision> pps : ppps) {
        aps.add(ActionProvision.createWithPrecursor(request, precursor, pps));
      }
      return aps;
    }
  }

  private Set<ActionProvision> createActionProvisionsWithoutPrecursor(final Action request) {
    final Set<Property> ps = request.getPreConditions().getFilledProperties();

    if (ps.isEmpty()) {
      return Collections.emptySet();
    } else {
      final ProductSet<PropertyProvision> ppps = findPropertyProvisions(ps);
      if (ppps == null) {
        return Collections.emptySet();
      }
      final Set<ActionProvision> aps = new HashSet<>();
      for (final Set<PropertyProvision> pps : ppps) {
        aps.add(ActionProvision.createWithoutPrecursor(request, pps));
      }
      return aps;
    }
  }

  private ProductSet<PropertyProvision> findPropertyProvisions(final Set<Property> ps) {
    final Set<Set<PropertyProvision>> ppss = new HashSet<>();

    // Find provisions for each required property.
    for (final Property p : ps) {
      final Set<PropertyProvision> pps = repository.provideCompatibleProperties(p);

      // There must be at least one provision per required property, otherwise an action cannot be enabled.
      if (pps.isEmpty()) {
        return null;
      }

      ppss.add(pps);
    }

    return new ProductSet<>(ppss);
  }

}
