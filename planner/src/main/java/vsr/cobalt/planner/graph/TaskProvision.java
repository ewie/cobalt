/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import vsr.cobalt.models.RealizedTask;
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
   * @param request a requested task
   * @param offer   a realized task
   */
  public TaskProvision(final Task request, final RealizedTask offer) {
    super(request, offer);
  }

  /**
   * Create a new task provision using the offered task as requested task.
   *
   * @param offer a realized task
   */
  public TaskProvision(final RealizedTask offer) {
    super(offer);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof TaskProvision;
  }

}
