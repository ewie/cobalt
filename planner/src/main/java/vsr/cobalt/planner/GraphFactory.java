/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import vsr.cobalt.models.Mashup;
import vsr.cobalt.planner.graph.Graph;

/**
 * A graph factory creates an initial graph from a mashup description.
 *
 * @author Erik Wienhold
 */
public interface GraphFactory {

  /**
   * Create an initial graph from a given mashup description.
   *
   * @param mashup a mashup description
   *
   * @return a new initial graph
   *
   * @throws PlanningException when the goal cannot be fully satisfied
   */
  Graph createGraph(Mashup mashup) throws PlanningException;

}
