package vsr.cobalt.models;

import java.util.Set;

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
   * Get action tasks compatible to a requested task.
   *
   * @param task the requested task
   *
   * @return a set of zero or more realized tasks
   */
  Set<RealizedTask> findCompatibleTasks(Task task);

  /**
   * Get action properties compatible to a requested property.
   *
   * @param property the requested property
   *
   * @return a set of zero or more published properties
   */
  Set<PublishedProperty> findCompatibleProperties(Property property);

}
