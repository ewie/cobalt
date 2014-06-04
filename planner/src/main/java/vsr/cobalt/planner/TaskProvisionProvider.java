/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.TaskProvision;

/**
 * Provides task provisions for a set of requested tasks.
 *
 * @author Erik Wienhold
 */
public interface TaskProvisionProvider {

  /**
   * Get task provisions each offering a task compatible with any of the given tasks.
   *
   * @param tasks a set of requested tasks
   *
   * @return a set of task provisions
   */
  Set<TaskProvision> getProvisionsFor(Set<Task> tasks);

}
