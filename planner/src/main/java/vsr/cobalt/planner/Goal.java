package vsr.cobalt.planner;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.planner.models.Task;

/**
 * A goal specifies information about the UI mashup to be conceived by the planner.
 *
 * @author Erik Wienhold
 */
public final class Goal {

  /**
   * A non-empty set of tasks to be realized by a UI mashup.
   */
  private final ImmutableSet<Task> tasks;

  /**
   * Create a new goal.
   *
   * @param tasks a non-empty set of tasks to be realized by a UI mashup
   */
  public Goal(final ImmutableSet<Task> tasks) {
    if (tasks.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more tasks");
    }
    this.tasks = tasks;
  }

  /**
   * @return the non-empty set of tasks to be realized by a UI mashup
   */
  public ImmutableSet<Task> getTasks() {
    return tasks;
  }

}
