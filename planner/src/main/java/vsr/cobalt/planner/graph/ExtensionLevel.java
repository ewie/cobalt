/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.planner.models.Action;

/**
 * A graph level representing an extension, i.e. it contains provisions for actions required by the previous level.
 *
 * @author Erik Wienhold
 */
public final class ExtensionLevel implements Level {

  /**
   * A non-empty set of action provisions.
   */
  private final ImmutableSet<ActionProvision> actionProvisions;

  /**
   * Create an extension level from a set of action provisions.
   *
   * @param actionProvisions a non-empty set of action provisions
   */
  public ExtensionLevel(final Set<ActionProvision> actionProvisions) {
    if (actionProvisions.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more provisions");
    }
    this.actionProvisions = ImmutableSet.copyOf(actionProvisions);
  }

  /**
   * @return the set of all action provisions
   */
  public Set<ActionProvision> getActionProvisions() {
    return actionProvisions;
  }

  /**
   * @return the set of precursor actions provided by all action provisions
   */
  @Override
  public Set<Action> getRequiredActions() {
    final Set<Action> as = new HashSet<>();
    for (final ActionProvision ap : actionProvisions) {
      as.addAll(ap.getProvidingActions());
      final Action a = ap.getPrecursorAction();
      if (a != null) {
        as.add(a);
      }
    }
    return as;
  }

  /**
   * @return the set of requested actions provided by all action provisions
   */
  public Set<Action> getRequestedActions() {
    final Set<Action> as = new HashSet<>();
    for (final ActionProvision ap : actionProvisions) {
      as.add(ap.getRequestedAction());
    }
    return as;
  }

  /**
   * Get all provisions which have the given requested action.
   *
   * @param action a requested action
   *
   * @return a set of provisions with the given requested action
   */
  public Set<ActionProvision> getActionProvisionsByRequestedAction(final Action action) {
    final Set<ActionProvision> aes = new HashSet<>();
    for (final ActionProvision ap : actionProvisions) {
      if (action.equals(ap.getRequestedAction())) {
        aes.add(ap);
      }
    }
    return aes;
  }

  /**
   * Test if this level can extend another level.
   * <p/>
   * An extension is possible if the requested actions are a subset of the other level's required actions.
   *
   * @param other another level to extend
   *
   * @return true when extension is possible, false otherwise
   */
  public boolean canExtendOn(final Level other) {
    final Set<Action> requests = getRequestedActions();
    final Set<Action> precursors = other.getRequiredActions();
    for (final Action request : requests) {
      if (!precursors.contains(request)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return actionProvisions.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof ExtensionLevel
        && equals((ExtensionLevel) other);
  }

  private boolean equals(final ExtensionLevel other) {
    return Objects.equals(actionProvisions, other.actionProvisions);
  }

}
