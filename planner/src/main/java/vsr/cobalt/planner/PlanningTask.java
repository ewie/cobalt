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
 * Realizes the planning process as an alternating sequence between graph extension and plan extraction.
 *
 * @author Erik Wienhold
 */
public class PlanningTask {

  private final PlanningProblem problem;

  private final GraphFactory factory;

  private final GraphExtender extender;

  private final PlanExtractor extractor;

  private final PlanCollector collector;

  private int targetDepth;

  private Graph graph;

  /**
   * Create a planning task.
   *
   * @param problem   a planning problem
   * @param factory   a graph factory
   * @param extender  a graph extender
   * @param extractor a plan extractor
   * @param collector a plan collector
   */
  public PlanningTask(final PlanningProblem problem, final GraphFactory factory, final GraphExtender extender,
                      final PlanExtractor extractor, final PlanCollector collector) {
    this.problem = problem;
    this.factory = factory;
    this.extender = extender;
    this.extractor = extractor;
    this.collector = collector;
    targetDepth = problem.getMinDepth();
  }

  /**
   * Create a planning task with an existing graph of arbitrary depth.
   *
   * @param problem   a planning problem
   * @param graph     a graph of arbitrary depth
   * @param extender  a graph extender
   * @param extractor a plan extractor
   * @param collector a plan collector
   */
  public PlanningTask(final PlanningProblem problem, final Graph graph, final GraphExtender extender,
                      final PlanExtractor extractor, final PlanCollector collector) {
    this(problem, createDummyFactory(graph), extender, extractor, collector);
  }

  /**
   * @return the planning problem
   */
  public PlanningProblem getProblem() {
    return problem;
  }

  /**
   * @return the graph factory, may be null
   */
  public GraphFactory getFactory() {
    return factory;
  }

  /**
   * @return the graph extender
   */
  public GraphExtender getExtender() {
    return extender;
  }

  /**
   * @return the plan extractor
   */
  public PlanExtractor getExtractor() {
    return extractor;
  }

  /**
   * @return the plan collector
   */
  public PlanCollector getCollector() {
    return collector;
  }

  /**
   * @return the current graph
   */
  public Graph getGraph() {
    return graph;
  }

  /**
   * Check if the planning process is done, i.e. there are no more plans to extract.
   *
   * @return true when the planner is done, false otherwise
   */
  public boolean isDone() {
    return graph != null && (targetDepth > problem.getMaxDepth() || !isExtendable());
  }

  /**
   * Advance in the planning process, i.e. create/extend the graph and extract plans.
   *
   * @throws PlanningException
   */
  public void advance() throws PlanningException {
    evolveGraph();
    extractPlans();
  }

  /**
   * Extend {@link #graph} to reach a depth of {@link #targetDepth}.
   *
   * @throws PlanningException
   */
  private void evolveGraph() throws PlanningException {
    if (graph == null) {
      graph = factory.createGraph(problem.getGoalMashup());
    }
    while (isExtendable()) {
      graph = extender.extendGraph(graph);
    }
  }

  /**
   * Extract and collect plans from {@link #graph}.
   */
  private void extractPlans() {
    final Iterator<Plan> plans = extractor.extractPlans(graph, targetDepth);
    while (plans.hasNext()) {
      collector.collect(plans.next());
    }
    targetDepth += 1;
  }

  /**
   * @return true when the {@link #graph} can be extended
   */
  private boolean isExtendable() {
    return graph.getDepth() < targetDepth && !graph.isSatisfied();
  }

  private static GraphFactory createDummyFactory(final Graph graph) {
    return new GraphFactory() {
      @Override
      public Graph createGraph(final Mashup mashup) {
        return graph;
      }
    };
  }

}
