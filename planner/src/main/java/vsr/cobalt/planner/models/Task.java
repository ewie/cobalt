package vsr.cobalt.planner.models;

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
