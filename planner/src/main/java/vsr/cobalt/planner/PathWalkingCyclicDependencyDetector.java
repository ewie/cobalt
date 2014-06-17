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

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.models.Action;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;

/**
 * Checks if an action causes a cyclic dependency by searching a path, via precursor actions and providing actions,
 * from the action to test to some action in the graph which the action to test {@link Action#represents(Action)
 * represents}.
 *
 * @author Erik Wienhold
 */
public class PathWalkingCyclicDependencyDetector implements CyclicDependencyDetector {

  @Override
  public boolean createsCyclicDependencyVia(final Action support, final Action dependent, final Graph graph) {
    if (support.represents(dependent)) {
      return true;
    }
    Set<Action> dependents = ImmutableSet.of(dependent);
    for (final ExtensionLevel xl : graph.getExtensionLevelsReversed()) {
      dependents = getDependentActions(dependents, xl);
      for (final Action a : dependents) {
        if (support.represents(a)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Get all requested actions of a given extension level which are dependent on any given action.
   *
   * @param supports a set of supporting actions
   * @param level    an extension level
   *
   * @return a set of dependent actions
   */
  private Set<Action> getDependentActions(final Set<Action> supports, final ExtensionLevel level) {
    final Set<Action> dependents = new HashSet<>();
    for (final ActionProvision ap : level.getActionProvisions()) {
      if (isDependent(ap, supports)) {
        dependents.add(ap.getRequestedAction());
      }
    }
    return dependents;
  }

  private boolean isDependent(final ActionProvision provider, final Set<Action> supports) {
    return !Collections.disjoint(provider.getRequiredActions(), supports);
  }

}
