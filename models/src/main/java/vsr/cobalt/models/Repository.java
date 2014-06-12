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

  /**
   * Get the distance between two tasks.
   * <p/>
   * The distance is measured as the number of inheritance steps from the offered task O up to the requested task R.
   * For identical tasks the distance is 0. When O subsumes R the distance is undefined.
   *
   * @param request a requested task
   * @param offer   an offered task
   *
   * @return the distance between the tasks, -1 when not compatible
   */
  int getDistance(Task request, Task offer);

  /**
   * Get the distance between two types.
   * <p/>
   * The distance is measured as the number of inheritance steps from the offered type O up to the requested type R.
   * For identical types the distance is 0. When O subsumes R the distance is undefined.
   *
   * @param request a requested type
   * @param offer   an offered type
   *
   * @return the distance between the types, -1 when not compatible
   */
  int getDistance(Type request, Type offer);

}
