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
public class PlanningProcess {

  private final MashupPlanner planner;

  private final PlanningProblem problem;

  private final PlanCollector collector;

  /**
   * The graph updated with each planning step.
   */
  private Graph graph;

  /**
   * The plan depth for the next planning step.
   * May overflow when {@link PlanningProblem#MAX_DEPTH} is reached.
   */
  private int targetDepth;

  /**
   * Indicates whether the planning process is done.
   */
  private boolean isDone;

  /**
   * Create a planning process.
   *
   * @param planner   a planner
   * @param collector a plan collector
   * @param problem   a planning problem
   */
  public PlanningProcess(final MashupPlanner planner, final PlanCollector collector,
                         final PlanningProblem problem) {
    this.problem = problem;
    this.planner = planner;
    this.collector = collector;
    targetDepth = problem.getMinDepth();
  }

  /**
   * Create a planning process with an existing graph of arbitrary depth.
   * <p/>
   * In this case the planner's graph factory is not required.
   *
   * @param planner   a planner
   * @param collector a plan collector
   * @param problem   a planning problem
   * @param graph     a graph of arbitrary depth
   */
  public PlanningProcess(final MashupPlanner planner, final PlanCollector collector, final PlanningProblem problem,
                         final Graph graph) {
    this(planner, collector, problem);
    this.graph = graph;
  }

  /**
   * @return the planner
   */
  public MashupPlanner getPlanner() {
    return planner;
  }

  /**
   * @return the plan collector
   */
  public PlanCollector getCollector() {
    return collector;
  }

  /**
   * @return the planning problem
   */
  public PlanningProblem getProblem() {
    return problem;
  }

  /**
   * @return the current graph, may be null
   */
  public Graph getGraph() {
    return graph;
  }

  /**
   * Check if the planning process is done, i.e. there are no more plans to extract or an error occured.
   *
   * @return true when the planning process is done, false otherwise
   */
  public boolean isDone() {
    return isDone || (isDone = checkIfDone());
  }

  /**
   * Advance in the planning process, i.e. create/extend the graph and extract plans.
   *
   * @throws PlanningException when the graph cannot be created or extended, the maximal plan depth is exceeded,
   *                           or the planning process is done
   */
  public void advance() throws PlanningException {
    // The max depth is exceeded when target depth overflowed. Also check if the target depth exceeds max depth by
    // value, in case max depth is not constrained by data type limitations.
    if (targetDepth < 0 || targetDepth > PlanningProblem.MAX_DEPTH) {
      throw new PlanningException("maximal plan depth exceeded");
    }

    if (isDone()) {
      throw new PlanningException("planning process is already done");
    }

    try {
      evolveGraph();
      extractPlans();
    } catch (final Exception ex) {
      // we're done on the first error
      isDone = true;
      throw ex;
    }

    // increase target depth for next graph extension
    targetDepth += 1;
  }

  /**
   * Extend {@link #graph} to reach a depth of {@link #targetDepth}.
   *
   * @throws PlanningException when the graph cannot be created or extended
   */
  private void evolveGraph() throws PlanningException {
    if (graph == null) {
      graph = planner.createGraph(problem.getGoalMashup());
    }
    while (isExtendable()) {
      graph = planner.extendGraph(graph);
    }
  }

  /**
   * Extract and collect plans from {@link #graph}.
   */
  private void extractPlans() {
    final Iterator<Plan> plans = planner.extractPlans(graph, targetDepth);

    collect:
    while (plans.hasNext()) {
      switch (collector.collect(plans.next())) {
      case SKIP_LEVEL:
        break collect;
      case STOP:
        isDone = true;
        break collect;
      }
    }
  }

  /**
   * @return true when the {@link #graph} can be extended, false otherwise
   */
  private boolean isExtendable() {
    return graph.getDepth() < targetDepth && !graph.isSatisfied();
  }

  /**
   * @return true when the planning process is done, false otherwise
   */
  private boolean checkIfDone() {
    // a graph must have been created before the planning process is done
    return graph != null && (isExceeded() || isSearchComplete());
  }

  /**
   * @return true when the planning problem has been exceeded, false otherwise
   */
  private boolean isExceeded() {
    return targetDepth > problem.getMaxDepth();
  }

  /**
   * Test whether there are more plans to be searched.
   * <p/>
   * There are no more plans to search when the graph is satisfied and plans up to the graph's depth have been
   * searched ({@link #targetDepth} exceeds {@link #graph} depth after the very last plans have been searched).
   *
   * @return true when all possible plans have been searched, false otherwise
   */
  private boolean isSearchComplete() {
    return graph.getDepth() < targetDepth && graph.isSatisfied();
  }

}
