/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Task;

/**
 * A task provision provider which simply delegates every task request to a repository and collects all found
 * provisions.
 *
 * @author Erik Wienhold
 */
public class BasicTaskProvisionProvider implements TaskProvisionProvider {

  private final Repository repository;

  /**
   * @param repository the repository to delegate to
   */
  public BasicTaskProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  public Set<TaskProvision> getProvisionsFor(final Set<Task> tasks) {
    final Set<TaskProvision> tps = new HashSet<>();
    for (final Task t : tasks) {
      tps.addAll(repository.findCompatibleTasks(t));
    }
    return tps;
  }

}
