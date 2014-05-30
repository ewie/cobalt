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
import vsr.cobalt.planner.models.Task;

/**
 * A graph level representing the initial required actions determined by task provisions.
 *
 * @author Erik Wienhold
 */
public final class InitialLevel implements Level {

  /**
   * A non-empty set of task provisions.
   */
  private final ImmutableSet<TaskProvision> taskProvisions;

  /**
   * Create an initial level from a set of task provisions.
   *
   * @param taskProvisions a non-empty set of task provisions
   */
  public InitialLevel(final Set<TaskProvision> taskProvisions) {
    if (taskProvisions.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more task provisions");
    }
    this.taskProvisions = ImmutableSet.copyOf(taskProvisions);
  }

  /**
   * @return the set of task provisions
   */
  public Set<TaskProvision> getTaskProvisions() {
    return taskProvisions;
  }

  public Set<Task> getRequestedTasks() {
    final Set<Task> ts = new HashSet<>();
    for (final TaskProvision tp : taskProvisions) {
      ts.add(tp.getRequest());
    }
    return ts;
  }

  /**
   * Get all task provisions satisfying the requested task
   *
   * @param task the requested task
   *
   * @return a set of task provisions with the requested task
   */
  public Set<TaskProvision> getTaskProvisionsByRequestedTask(final Task task) {
    final Set<TaskProvision> tps = new HashSet<>();
    for (final TaskProvision tp : taskProvisions) {
      if (tp.getRequest().equals(task)) {
        tps.add(tp);
      }
    }
    return tps;
  }

  /**
   * @return the set of required actions provided by all task provisions
   */
  @Override
  public Set<Action> getRequiredActions() {
    final Set<Action> as = new HashSet<>();
    for (final TaskProvision tp : taskProvisions) {
      as.add(tp.getProvidingAction());
    }
    return as;
  }

  @Override
  public int hashCode() {
    return taskProvisions.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof InitialLevel
        && equals((InitialLevel) other);
  }

  private boolean equals(final InitialLevel other) {
    return Objects.equals(taskProvisions, other.taskProvisions);
  }

}
