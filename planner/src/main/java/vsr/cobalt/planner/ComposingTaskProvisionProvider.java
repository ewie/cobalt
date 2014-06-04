/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.TaskProvision;

/**
 * @author Erik Wienhold
 */
public class ComposingTaskProvisionProvider
    extends ComposingProvisionProvider<Task, TaskProvision>
    implements TaskProvisionProvider {

  private final Repository repository;

  public ComposingTaskProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  protected Set<TaskProvision> findProvisionsFor(final Task task) {
    return createProvisions(task, repository.findCompatibleTasks(task));
  }

  @Override
  protected TaskProvision createProvision(final Task request, final Task offer, final Action action) {
    return new TaskProvision(request, offer, action);
  }

  @Override
  protected Set<Task> getOffers(final Action action) {
    return action.getRealizedTasks();
  }

  private static Set<TaskProvision> createProvisions(final Task request, final Set<RealizedTask>
      realizedTasks) {
    final Set<TaskProvision> provisions = new HashSet<>(realizedTasks.size());
    for (final RealizedTask rt : realizedTasks) {
      provisions.add(createProvision(request, rt));
    }
    return provisions;
  }

  private static TaskProvision createProvision(final Task request, final RealizedTask realized) {
    return new TaskProvision(request, realized.getTask(), realized.getAction());
  }

}
