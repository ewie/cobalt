package vsr.cobalt.planner;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.Task;

/**
 * A repository serves as the knowledge base for the planning algorithm.
 *
 * @author Erik Wienhold
 */
public interface Repository {

  /**
   * Get task provisions satisfying a requested task.
   *
   * @param task the requested task
   *
   * @return a set of zero or more task provisions
   */
  public ImmutableSet<TaskProvision> realizeCompatibleTasks(Task task);

  /**
   * Get precursor actions for the given action.
   *
   * @param action an action
   *
   * @return a set of zero or more precursor actions
   *
   * @see Action#canBePrecursorOf(Action)
   */
  public ImmutableSet<Action> findPrecursors(Action action);

  /**
   * Get property provisions satisfying a requested property.
   *
   * @param property the requested property
   *
   * @return a set of zero or more property provisions
   */
  public ImmutableSet<PropertyProvision> provideCompatibleProperties(Property property);

}
