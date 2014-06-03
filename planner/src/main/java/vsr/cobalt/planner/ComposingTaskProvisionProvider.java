/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Task;

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
    return repository.findCompatibleTasks(task);
  }

  @Override
  protected TaskProvision createProvision(final Task request, final Task offer, final Action action) {
    return new TaskProvision(request, offer, action);
  }

  @Override
  protected Set<Task> getOffers(final Action action) {
    return action.getRealizedTasks();
  }

}
