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
 * @author Erik Wienhold
 */
public class Planner {

  private final GraphExtender extender;

  private final PlanIteratorFactory planIteratorFactory;

  private Graph graph;

  public Planner(final GraphExtender extender, final PlanIteratorFactory planIteratorFactory) {
    this.extender = extender;
    this.planIteratorFactory = planIteratorFactory;
  }

  public void createGraph(final Goal goal) throws PlanningException {
    graph = extender.createGraph(goal);
  }

  public void extendGraph() throws PlanningException {
    graph = extender.extendGraph(graph);
    Iterator<Plan> it = planIteratorFactory.createPlanIterator(graph);
  }

  public static interface PlanIteratorFactory {

    public Iterator<Plan> createPlanIterator(Graph graph);

  }

}
