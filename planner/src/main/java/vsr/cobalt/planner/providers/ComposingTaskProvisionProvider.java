/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

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
    extends ComposingProvisionProvider<Task, RealizedTask, TaskProvision>
    implements TaskProvisionProvider {

  private final Repository repository;

  public ComposingTaskProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  protected Set<RealizedTask> getOffersFor(final Task task) {
    return repository.findCompatibleTasks(task);
  }

  @Override
  protected TaskProvision createProvision(final Task request, final Task subject, final Action action) {
    return new TaskProvision(request, new RealizedTask(subject, action));
  }

  @Override
  protected Set<Task> getOfferedSubjects(final Action action) {
    return action.getRealizedTasks();
  }

}
