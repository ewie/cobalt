/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.rating;

import vsr.cobalt.planner.Plan;

/**
 * Provides a rating for plans.
 *
 * @author Erik Wienhold
 */
public interface PlanRater {

  /**
   * @param plan the plan to rate
   *
   * @return a rating for the plan or null when no such rating exists
   */
  Rating rate(Plan plan);

}
