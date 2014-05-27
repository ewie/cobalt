/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.makers;

import vsr.cobalt.planner.models.Task;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public class TaskMaker implements Maker<Task> {

  private final AtomicValue<String> identifier = new AtomicValue<>();

  public static TaskMaker aTask() {
    return new TaskMaker();
  }

  public static TaskMaker aMinimalTask() {
    return aTask().withIdentifier("");
  }

  @Override
  public Task make() {
    return new Task(identifier.get());
  }

  public TaskMaker withIdentifier(final String identifier) {
    this.identifier.set(identifier);
    return this;
  }

}
