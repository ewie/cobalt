package vsr.cobalt.planner;

import java.util.Set;

import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.Task;
import vsr.cobalt.planner.models.Widget;

/**
 * A repository serves as the knowledge base for the planning algorithm.
 *
 * @author Erik Wienhold
 */
public interface Repository {

  /**
   * Get all actions which belong to a given widget.
   *
   * @param widget a widget
   *
   * @return a set of all actions belonging to the given widget
   */
  Set<Action> getWidgetActions(Widget widget);

  /**
   * Get task provisions satisfying a requested task.
   *
   * @param task the requested task
   *
   * @return a set of zero or more task provisions
   */
  Set<TaskProvision> findCompatibleTasks(Task task);

  /**
   * Get property provisions satisfying a requested property.
   *
   * @param property the requested property
   *
   * @return a set of zero or more property provisions
   */
  Set<PropertyProvision> findCompatibleProperties(Property property);

}
