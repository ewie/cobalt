/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

/**
 * A task offered through realization.
 *
 * @author Erik Wienhold
 */
public final class RealizedTask extends Offer<Task> {

  /**
   * @param task   a task realized by an action
   * @param action an action realizing the task
   */
  public RealizedTask(final Task task, final Action action) {
    super(task, action);
    if (!action.realizes(task)) {
      throw new IllegalArgumentException("expecting task to be realized by given action");
    }
  }

  @Override
  public boolean canEqual(final Object other) {
    return other instanceof RealizedTask;
  }

}
