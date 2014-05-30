package vsr.cobalt.planner;

import java.util.Set;

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
  Set<TaskProvision> realizeCompatibleTasks(Task task);

  /**
   * Get precursor actions for the given action.
   *
   * @param action an action
   *
   * @return a set of zero or more precursor actions
   *
   * @see Action#canBePrecursorOf(Action)
   */
  Set<Action> findPrecursors(Action action);

  /**
   * Get property provisions satisfying a requested property.
   *
   * @param property the requested property
   *
   * @return a set of zero or more property provisions
   */
  Set<PropertyProvision> provideCompatibleProperties(Property property);

}
