/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Objects;

import vsr.cobalt.models.Mashup;

/**
 * A planning problem consisting of a goal mashup and a depth range for plans to be searched.
 *
 * @author Erik Wienhold
 */
public class PlanningProblem {

  /**
   * The minimal required plan depth.
   */
  public static final int MIN_DEPTH = 1;

  /**
   * The maximal allowed plan depth, effectively allowing plans of "infinite" depth.
   * Plans of maximal depth are unlikely, as the resulting mashup would be extremely large.
   */
  public static final int MAX_DEPTH = Integer.MAX_VALUE;

  private final Mashup goalMashup;

  private final int minDepth;

  private final int maxDepth;

  /**
   * Create a planning problem.
   *
   * @param goalMashup the goal mashup
   * @param minDepth   the minimum plan depth
   * @param maxDepth   the maximum plan depth
   */
  public PlanningProblem(final Mashup goalMashup, final int minDepth, final int maxDepth) {
    this.goalMashup = goalMashup;
    this.minDepth = minDepth;
    this.maxDepth = maxDepth;
    if (minDepth < MIN_DEPTH) {
      throw new IllegalArgumentException("expecting positive minimum depth");
    }
    if (minDepth > maxDepth) {
      throw new IllegalArgumentException("expecting minimum depth to be less than or equal to maximum depth");
    }
  }

  /**
   * Create a planning problem with infinite plan depth.
   *
   * @param goalMashup the goal mashup
   * @param minDepth   the minimum plan depth
   */
  public PlanningProblem(final Mashup goalMashup, final int minDepth) {
    this(goalMashup, minDepth, MAX_DEPTH);
  }

  /**
   * Create a planning problem without any plan depth constraint.
   *
   * @param goalMashup the goal mashup
   */
  public PlanningProblem(final Mashup goalMashup) {
    this(goalMashup, MIN_DEPTH);
  }

  /**
   * @return the goal mashup
   */
  public Mashup getGoalMashup() {
    return goalMashup;
  }

  /**
   * @return the minimum plan depth
   */
  public int getMinDepth() {
    return minDepth;
  }

  /**
   * @return the maximum plan depth
   */
  public int getMaxDepth() {
    return maxDepth;
  }

  @Override
  public int hashCode() {
    return Objects.hash(goalMashup, minDepth, maxDepth);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof PlanningProblem
        && equals((PlanningProblem) other);
  }

  private boolean equals(final PlanningProblem other) {
    return minDepth == other.minDepth
        && maxDepth == other.maxDepth
        && goalMashup.equals(other.goalMashup);
  }

}
