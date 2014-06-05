/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph.makers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.makers.ActionMaker;
import vsr.cobalt.models.makers.TaskMaker;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;

/**
 * @author Erik Wienhold
 */
public class TaskProvisionMaker implements Maker<TaskProvision> {

  private final AtomicValue<Task> request = new AtomicValue<>();

  private final AtomicValue<Task> offer = new AtomicValue<>();

  private final AtomicValue<Action> action = new AtomicValue<>();

  public static TaskProvisionMaker aTaskProvision() {
    return new TaskProvisionMaker();
  }

  public static TaskProvisionMaker aMinimalTaskProvision() {
    final TaskMaker task = aMinimalTask();
    final ActionMaker action = aMinimalAction().withTask(task);
    return aTaskProvision()
        .withProvidingAction(action)
        .withOffer(task)
        .withRequest(task);
  }

  @Override
  public TaskProvision make() {
    return new TaskProvision(request.get(), new RealizedTask(offer.get(), action.get()));
  }

  public TaskProvisionMaker withRequest(final Maker<Task> maker) {
    request.set(maker);
    return this;
  }

  public TaskProvisionMaker withRequest(final Task task) {
    request.set(task);
    return this;
  }

  public TaskProvisionMaker withOffer(final Maker<Task> maker) {
    offer.set(maker);
    return this;
  }

  public TaskProvisionMaker withOffer(final Task task) {
    offer.set(task);
    return this;
  }

  public TaskProvisionMaker withProvidingAction(final Maker<Action> maker) {
    action.set(maker);
    return this;
  }

  public TaskProvisionMaker withProvidingAction(final Action action) {
    this.action.set(action);
    return this;
  }

}
