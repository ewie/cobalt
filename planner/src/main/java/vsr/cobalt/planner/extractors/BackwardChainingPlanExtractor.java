/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extractors;

import vsr.cobalt.planner.PlanExtractor;
import vsr.cobalt.planner.graph.Graph;

/**
 * Extract plans from a graph using backward chaining.
 *
 * @author Erik Wienhold
 */
public class BackwardChainingPlanExtractor implements PlanExtractor {

  @Override
  public BackwardChainingPlanIterator extractPlans(final Graph graph, final int depth) {
    return new BackwardChainingPlanIterator(graph, depth, depth);
  }

}
