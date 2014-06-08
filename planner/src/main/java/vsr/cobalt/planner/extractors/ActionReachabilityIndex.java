/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extractors;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;
import vsr.cobalt.models.Action;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.Level;

/**
 * Records which actions are reachable, i.e. the actions whose pre-conditions are empty or satisfied by some reachable
 * actions in the previous level.
 *
 * @author Erik Wienhold
 */
class ActionReachabilityIndex {

  /**
   * Map levels to their respective reachable actions.
   */
  public ImmutableSetMultimap<Level, Action> index;

  /**
   * Create a new reachability index for the given graph.
   *
   * @param graph a graph
   */
  public ActionReachabilityIndex(final Graph graph) {
    index = buildIndex(graph);
  }

  /**
   * Check if an actions is reachable in a given level.
   *
   * @param action an action to check
   * @param level  a level containing the given action
   *
   * @return true when reachable, false otherwise
   */
  public boolean isReachable(final Action action, final Level level) {
    return index.containsEntry(level, action);
  }

  private static ImmutableSetMultimap<Level, Action> buildIndex(final Graph graph) {
    final ImmutableSetMultimap.Builder<Level, Action> b = ImmutableSetMultimap.builder();

    Set<Action> enabledActions = ImmutableSet.of();

    // Iterate over all extension levels in reverse order, i.e. start with the last level.
    for (final ExtensionLevel xl : graph.getExtensionLevels()) {
      // Combine the enabled actions of the previous level with the enabled actions of the current level.
      enabledActions = getCombinedEnabledActions(xl, enabledActions);

      // Associate the actions previously determined to be enabled with the current level.
      b.putAll(xl, enabledActions);

      // Get all actions enabled by the current level.
      // These are a subset of enabled actions in the next level.
      enabledActions = getEnabledTargetActions(xl, enabledActions);
    }

    // The initial level remains.
    final InitialLevel il = graph.getInitialLevel();
    enabledActions = getCombinedEnabledActions(il, enabledActions);
    b.putAll(il, enabledActions);

    return b.build();
  }

  private static Set<Action> getCombinedEnabledActions(final Level level, final Set<Action> enabledActions) {
    final Set<Action> as = new HashSet<>();
    for (final Action a : level.getRequiredActions()) {
      if (a.isEnabled()) {
        as.add(a);
      }
    }
    return Sets.union(as, enabledActions);
  }

  private static Set<Action> getEnabledTargetActions(final ExtensionLevel level, final Set<Action> enabledActions) {
    final Set<Action> as = new HashSet<>();
    for (final ActionProvision ap : level.getActionProvisions()) {
      if (isEnabled(ap, enabledActions)) {
        as.add(ap.getRequestedAction());
      }
    }
    return as;
  }

  private static boolean isEnabled(final ActionProvision actionProvision, final Set<Action> enabledActions) {
    for (final Action a : actionProvision.getRequiredActions()) {
      if (!enabledActions.contains(a)) {
        return false;
      }
    }
    return true;
  }

}
