/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import vsr.cobalt.planner.graph.Graph;

/**
 * A graph extender takes a not fully satisfied graph and extends it by one level at a time.
 *
 * @author Erik Wienhold
 */
public interface GraphExtender {

  /**
   * Extend a graph by one level.
   *
   * @param graph a planning graph to extend
   *
   * @return an extended planning graph
   *
   * @throws PlanningException when the last level cannot be satisfied
   */
  Graph extendGraph(Graph graph) throws PlanningException;

}
