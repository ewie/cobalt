/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.rating;

import vsr.cobalt.models.Action;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.Level;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;

/**
 * A visitor used by {@link TraversingPlanRater} to visit and rate each graph element.
 * <p/>
 * The default implementations return a neutral rating value of 0.
 *
 * @author Erik Wienhold
 */
public class PlanRaterVisitor {

  /**
   * @param graph the graph to rate
   *
   * @return the rating value or null to indicate no rating
   */
  Integer rateGraph(final Graph graph) {
    return 0;
  }

  /**
   * @param level the level to rate
   *
   * @return the rating value or null to indicate no rating
   */
  Integer rateLevel(final Level level) {
    return 0;
  }

  /**
   * @param action the action to rate
   *
   * @return the rating value or null to indicate no rating
   */
  Integer rateAction(final Action action) {
    return 0;
  }

  /**
   * @param provision the task provision to rate
   *
   * @return the rating value or null to indicate no rating
   */
  Integer rateTaskProvision(final TaskProvision provision) {
    return 0;
  }

  /**
   * @param provision the action provision to rate
   *
   * @return the rating value or null to indicate no rating
   */
  Integer rateActionProvision(final ActionProvision provision) {
    return 0;
  }

  /**
   * @param provision the property provision to rate
   *
   * @return the rating value or null to indicate no rating
   */
  Integer ratePropertyProvision(final PropertyProvision provision) {
    return 0;
  }

}
