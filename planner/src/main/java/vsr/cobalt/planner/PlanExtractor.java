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
 * Extract plans from a graph.
 *
 * @author Erik Wienhold
 */
public interface PlanExtractor {

  /**
   * Extract plans of a specific depth from a graph.
   *
   * @param graph a graph
   * @param depth the depth of extracted plans
   *
   * @return an iterator of extracted plans
   */
  Iterator<Plan> extractPlans(final Graph graph, final int depth);

}
