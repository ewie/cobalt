/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Task;

/**
 * @author Erik Wienhold
 */
public class TaskMaker extends IdentifiableMaker<Task> {

  public static TaskMaker aTask() {
    return new TaskMaker();
  }

  public static TaskMaker aMinimalTask() {
    return (TaskMaker) aTask().withIdentifier("");
  }

  @Override
  public Task make() {
    return new Task(identifier.get());
  }

}
