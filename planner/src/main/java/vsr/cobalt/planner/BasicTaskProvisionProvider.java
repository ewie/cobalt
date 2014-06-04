/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.TaskProvision;

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
      tps.addAll(createProvisions(t, repository.findCompatibleTasks(t)));
    }
    return tps;
  }

  private static Collection<TaskProvision> createProvisions(final Task request,
                                                            final Collection<RealizedTask> realizedTasks) {
    final Collection<TaskProvision> provisions = new ArrayList<>(realizedTasks.size());
    for (final RealizedTask rt : realizedTasks) {
      provisions.add(createProvision(request, rt));
    }
    return provisions;
  }

  private static TaskProvision createProvision(final Task request, final RealizedTask realized) {
    return new TaskProvision(request, realized.getTask(), realized.getAction());
  }

}
