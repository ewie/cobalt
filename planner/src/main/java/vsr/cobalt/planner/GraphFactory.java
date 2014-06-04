/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.TaskProvision;

/**
 * A graph factory creates an initial graph from a goal.
 *
 * @author Erik Wienhold
 */
public class GraphFactory {

  private final TaskProvisionProvider taskProvisionProvider;

  /**
   * @param taskProvisionProvider a provider of task provisions
   */
  public GraphFactory(final TaskProvisionProvider taskProvisionProvider) {
    this.taskProvisionProvider = taskProvisionProvider;
  }

  /**
   * Create an initial graph from a given goal.
   *
   * @param goal a goal
   *
   * @return a new initial graph
   *
   * @throws PlanningException when the goal cannot be fully satisfied
   */
  public Graph createGraph(final Goal goal) throws PlanningException {
    final Set<Task> ts = goal.getTasks();

    final Set<TaskProvision> tps = taskProvisionProvider.getProvisionsFor(ts);

    if (tps.isEmpty() || !satisfyAllTasks(tps, ts)) {
      throw new PlanningException("cannot realize all goal tasks");
    }

    return Graph.create(new InitialLevel(tps));
  }

  /**
   * Check if a set of task provisions satisfy all tasks.
   *
   * @param provisions a set of task provisions
   * @param tasks      a set of requested tasks
   *
   * @return true when all tasks are satisfied, false otherwise
   */
  private boolean satisfyAllTasks(final Set<TaskProvision> provisions, final Set<Task> tasks) {
    final Set<Task> satisfiedTasks = new HashSet<>();

    for (final TaskProvision tp : provisions) {
      satisfiedTasks.add(tp.getRequest());
    }

    return satisfiedTasks.equals(tasks);
  }

}
