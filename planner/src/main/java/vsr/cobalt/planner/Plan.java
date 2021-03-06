/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.planner.graph.ActionMutexIndex;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.Level;

/**
 * A plan is the result of a planning process. It wraps a graph, resulting from the planning process, containing only
 * satisfied actions and functionalities.
 *
 * @author Erik Wienhold
 */
public final class Plan {

  /**
   * The graph resulting from a planning process.
   */
  private final Graph graph;

  /**
   * Create a new plan.
   *
   * @param graph a graph with only satisfied actions and functionalities
   */
  public Plan(final Graph graph) {
    assertGraph(graph);
    this.graph = graph;
  }

  /**
   * @return the graph
   */
  public Graph getGraph() {
    return graph;
  }

  @Override
  public int hashCode() {
    return graph.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Plan
        && equals((Plan) other);
  }

  private boolean equals(final Plan other) {
    return Objects.equals(graph, other.graph);
  }

  private static void assertGraph(final Graph graph) {
    final InitialLevel il = graph.getInitialLevel();

    assertInitialLevel(il);

    Set<Action> actions = Collections.emptySet();

    for (final ExtensionLevel xl : graph.getExtensionLevelsReversed()) {
      assertRequestedActions(xl);
      assertRequiredActions(xl, actions);
      actions = xl.getRequestedActions();
    }

    assertRequiredActions(il, actions);

    assertMutexAction(graph);
  }

  private static void assertInitialLevel(final InitialLevel level) {
    for (final Functionality f : level.getRequestedFunctionalities()) {
      if (level.getFunctionalityProvisionsByRequestedFunctionality(f).size() > 1) {
        throw new IllegalArgumentException("expecting graph with single provision for each requested functionality");
      }
    }
  }

  private static void assertRequestedActions(final ExtensionLevel level) {
    for (final Action a : level.getRequestedActions()) {
      if (level.getActionProvisionsByRequestedAction(a).size() > 1) {
        throw new IllegalArgumentException("expecting graph with single provision for each requested action");
      }
    }
  }

  private static void assertRequiredActions(final Level level, final Set<Action> requestedActions) {
    for (final Action a : level.getRequiredActions()) {
      if (!requestedActions.contains(a)) {
        if (!a.getPreConditions().isEmpty()) {
          throw new IllegalArgumentException("expecting graph with only satisfied actions");
        }
      }
    }
  }

  private static void assertMutexAction(final Graph graph) {
    final ActionMutexIndex ami = new ActionMutexIndex(graph);
    if (ami.hasAnyMutexes()) {
      throw new IllegalArgumentException("expecting graph with only non-mutex actions");
    }
  }

}
