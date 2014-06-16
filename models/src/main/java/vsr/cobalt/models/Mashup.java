/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Describes a desired mashup.
 *
 * @author Erik Wienhold
 */
public final class Mashup {

  /**
   * A non-empty set of tasks to be realized by the mashup.
   */
  private final Set<Task> tasks;

  /**
   * Create a new mashup.
   *
   * @param tasks a non-empty set of tasks to be realized by the mashup
   */
  public Mashup(final Set<Task> tasks) {
    if (tasks.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more tasks");
    }
    this.tasks = ImmutableSet.copyOf(tasks);
  }

  /**
   * @return the non-empty set of tasks to be realized by this mashup
   */
  public Set<Task> getTasks() {
    return tasks;
  }

  @Override
  public int hashCode() {
    return tasks.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Mashup
        && equals((Mashup) other);
  }

  private boolean equals(final Mashup other) {
    return tasks.equals(other.tasks);
  }

}
