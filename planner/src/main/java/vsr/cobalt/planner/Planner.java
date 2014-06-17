/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Iterator;

import vsr.cobalt.planner.graph.Graph;

/**
 * Realizes the planning process as an alternating sequence between graph extension and plan extraction.
 *
 * @author Erik Wienhold
 */
public class Planner {

  private final GraphExtender extender;

  private final PlanExtractor extractor;

  private final PlanCollector collector;

  private final int maxDepth;

  /**
   * The depth the graph should be extended to.
   */
  private int targetDepth;

  /**
   * The graph from the most recent extension.
   */
  private Graph graph;

  /**
   * @param graph     a graph of arbitrary depth
   * @param extender  extends the graph by one level
   * @param extractor extract plans from a graph
   * @param collector a collector of extracted plans
   * @param minDepth  the minimum depth of extracted plans
   * @param maxDepth  the maximum depth of extracted plans
   */
  public Planner(final Graph graph, final GraphExtender extender, final PlanExtractor extractor,
                 final PlanCollector collector, final int minDepth, final int maxDepth) {
    if (minDepth < 1) {
      throw new IllegalArgumentException("expecting positive minimum depth");
    }
    if (minDepth > maxDepth) {
      throw new IllegalArgumentException("expecting minimum depth to be less than or equal to maximum depth");
    }
    this.extender = extender;
    this.extractor = extractor;
    this.collector = collector;
    this.maxDepth = maxDepth;
    this.graph = graph;
    targetDepth = minDepth;
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
    return targetDepth > maxDepth
        || targetDepth > graph.getDepth() && graph.isSatisfied();
  }

  /**
   * Advance in the planning process, i.e. extend the graph and extract plans.
   *
   * @throws PlanningException
   */
  public void advance() throws PlanningException {
    evolveGraph();
    extractPlans();
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
   * Extend {@link #graph} to reach a depth of {@link #targetDepth}.
   *
   * @throws PlanningException
   */
  private void evolveGraph() throws PlanningException {
    while (graph.getDepth() < targetDepth && !graph.isSatisfied()) {
      graph = extender.extendGraph(graph);
    }
  }

}
