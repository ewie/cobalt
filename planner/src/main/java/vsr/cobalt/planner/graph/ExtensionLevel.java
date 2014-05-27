/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Objects;

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
  public ExtensionLevel(final ImmutableSet<ActionProvision> actionProvisions) {
    if (actionProvisions.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more provisions");
    }
    this.actionProvisions = actionProvisions;
  }

  /**
   * @return the set of all action provisions
   */
  public ImmutableSet<ActionProvision> getActionProvisions() {
    return actionProvisions;
  }

  /**
   * @return the set of precursor actions provided by all action provisions
   */
  @Override
  public ImmutableSet<Action> getRequiredActions() {
    final ImmutableSet.Builder<Action> as = ImmutableSet.builder();
    for (final ActionProvision ap : actionProvisions) {
      as.addAll(ap.getProvidingActions());
      final Action a = ap.getPrecursorAction();
      if (a != null) {
        as.add(a);
      }
    }
    return as.build();
  }

  /**
   * @return the set of requested actions provided by all action provisions
   */
  public ImmutableSet<Action> getRequestedActions() {
    final ImmutableSet.Builder<Action> as = ImmutableSet.builder();
    for (final ActionProvision ap : actionProvisions) {
      as.add(ap.getRequestedAction());
    }
    return as.build();
  }

  /**
   * Get all provisions which have the given requested action.
   *
   * @param action a requested action
   *
   * @return a set of provisions with the given requested action
   */
  public ImmutableSet<ActionProvision> getActionProvisionsByRequestedAction(final Action action) {
    final ImmutableSet.Builder<ActionProvision> aes = ImmutableSet.builder();
    for (final ActionProvision ap : actionProvisions) {
      if (action.equals(ap.getRequestedAction())) {
        aes.add(ap);
      }
    }
    return aes.build();
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
    final ImmutableSet<Action> requests = getRequestedActions();
    final ImmutableSet<Action> precursors = other.getRequiredActions();
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
