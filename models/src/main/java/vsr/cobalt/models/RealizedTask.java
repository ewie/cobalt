/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

/**
 * A pair of a task and an action realizing the task.
 *
 * @author Erik Wienhold
 */
public class RealizedTask {

  private final Task task;

  private final Action action;

  /**
   * @param task   a task realized by an action
   * @param action an action realizing the task
   */
  public RealizedTask(final Task task, final Action action) {
    if (!action.realizes(task)) {
      throw new IllegalArgumentException("expecting task to be realized by given action");
    }
    this.task = task;
    this.action = action;
  }

  public Task getTask() {
    return task;
  }

  public Action getAction() {
    return action;
  }

  @Override
  public int hashCode() {
    return Objects.hash(task, action);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof RealizedTask
        && equals((RealizedTask) other);
  }

  private boolean equals(final RealizedTask other) {
    return Objects.equals(task, other.task)
        && Objects.equals(action, other.action);
  }

}
