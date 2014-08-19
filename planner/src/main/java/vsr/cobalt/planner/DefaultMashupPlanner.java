/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Iterator;

import vsr.cobalt.models.Mashup;
import vsr.cobalt.planner.graph.Graph;

/**
 * The default mashup planner realizes its behaviour by using an individual planning graph factory, extender,
 * and plan extractor.
 *
 * @author Erik Wienhold
 */
public class DefaultMashupPlanner implements MashupPlanner {

  private final GraphFactory factory;

  private final GraphExtender extender;

  private final PlanExtractor extractor;

  /**
   * @param factory   a planning graph factory
   * @param extender  a planning graph extender
   * @param extractor a plan extractor
   */
  public DefaultMashupPlanner(final GraphFactory factory,
                              final GraphExtender extender,
                              final PlanExtractor extractor) {
    this.factory = factory;
    this.extender = extender;
    this.extractor = extractor;
  }

  /**
   * @return the planning graph factory
   */
  public GraphFactory getFactory() {
    return factory;
  }

  /**
   * @return the planning graph extender
   */
  public GraphExtender getExtender() {
    return extender;
  }

  /**
   * @return the plan factory
   */
  public PlanExtractor getExtractor() {
    return extractor;
  }

  @Override
  public Graph createGraph(final Mashup mashup) throws PlanningException {
    return factory.createGraph(mashup);
  }

  @Override
  public Graph extendGraph(final Graph graph) throws PlanningException {
    return extender.extendGraph(graph);
  }

  @Override
  public Iterator<Plan> extractPlans(final Graph graph, final int depth) {
    return extractor.extractPlans(graph, depth);
  }

  /**
   * Create a planning process using this planner.
   *
   * @param problem   a planning problem
   * @param collector a plan collector
   */
  public PlanningProcess createPlanningProcess(final PlanningProblem problem, final PlanCollector collector) {
    return new PlanningProcess(this, collector, problem);
  }

}
