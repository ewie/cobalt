/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import vsr.cobalt.planner.graph.Graph;

/**
 * Extract plans from a graph using backward chaining.
 *
 * @author Erik Wienhold
 */
public class BackwardChainingPlanExtractor implements PlanExtractor {

  public BackwardChainingPlanIterator extractPlans(final Graph graph, final int depth) {
    return new BackwardChainingPlanIterator(graph, depth, depth);
  }

}
