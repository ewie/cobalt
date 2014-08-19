/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

/**
 * A planner is the combined strategy of graph creation, extension, and plan extraction.
 *
 * @author Erik Wienhold
 */
public class Planner {

  private final GraphFactory factory;

  private final GraphExtender extender;

  private final PlanExtractor extractor;

  /**
   * @param factory   a graph factory
   * @param extender  a graph extender
   * @param extractor a plan extractor
   */
  public Planner(final GraphFactory factory, final GraphExtender extender, final PlanExtractor extractor) {
    this.factory = factory;
    this.extender = extender;
    this.extractor = extractor;
  }

  /**
   * Create a planning task using this planner's strategy.
   *
   * @param problem   a planning problem
   * @param collector a plan collector
   */
  public PlanningTask createTask(final PlanningProblem problem, final PlanCollector collector) {
    return new PlanningTask(problem, factory, extender, extractor, collector);
  }

}
