/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.TaskProvision;

/**
 * Provides task provisions for a set of requested tasks.
 *
 * @author Erik Wienhold
 */
public interface TaskProvisionProvider extends ProvisionProvider<Task, TaskProvision> {
}
