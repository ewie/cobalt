/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Task;

/**
 * A task provision specifies how a requested task can be satisfied via a providing action realizing a matching task.
 *
 * @author Erik Wienhold
 */
public final class TaskProvision extends Provision<Task> {

  /**
   * Create a new task provision.
   *
   * @param request the requested task
   * @param offer   the provided task matching the requested task
   * @param action  the action realizing the providing task
   */
  public TaskProvision(final Task request, final Task offer, final Action action) {
    super(request, offer, action);
    if (!action.realizes(offer)) {
      throw new IllegalArgumentException("expecting the offered task to be realized by providing action");
    }
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof TaskProvision;
  }

}
