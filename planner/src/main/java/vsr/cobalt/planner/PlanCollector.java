/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

/**
 * Collects plans and instructs a planning process on how to proceed.
 *
 * @author Erik Wienhold
 */
public interface PlanCollector {

  /**
   * Add a plan to the collector.
   *
   * @param plan a plan
   *
   * @return a value indicating how a planner should proceed
   */
  Result collect(Plan plan);

  /**
   * States how the planning process should behave after collecting a plan.
   */
  enum Result {
    /**
     * The planning process should continue normally.
     */
    CONTINUE,

    /**
     * The planning process should skip the current level, i.e. ignore any further plans in the current level.
     */
    SKIP_LEVEL,

    /**
     * The planning process should be stopped entirely, ignoring any further plans.
     */
    STOP
  }

}
