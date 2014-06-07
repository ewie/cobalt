/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

/**
 * Collects plans.
 *
 * @author Erik Wienhold
 */
public interface PlanCollector {

  /**
   * Add a plan to the collector.
   *
   * @param plan a plan
   */
  void collect(Plan plan);

}
