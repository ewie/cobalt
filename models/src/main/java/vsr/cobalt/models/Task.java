/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

/**
 * A task represents a concept of uniquely identified functionality.
 *
 * @author Erik Wienhold
 */
public final class Task extends Identifiable {

  /**
   * Create a new task.
   *
   * @param identifier a task identifier
   */
  public Task(final String identifier) {
    super(identifier);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof Task;
  }

}
