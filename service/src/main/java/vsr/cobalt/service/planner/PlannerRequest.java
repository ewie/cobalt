/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.models.Mashup;

/**
 * @author Erik Wienhold
 */
public final class PlannerRequest {

  public static final int MIN_DEPTH = 1;

  public static final int MAX_DEPTH = Integer.MAX_VALUE;

  private final Mashup goalMashup;

  private final int minDepth;

  private final int maxDepth;

  public PlannerRequest(final Mashup goalMashup, final int minDepth, final int maxDepth) {
    if (goalMashup == null) {
      throw new IllegalArgumentException("expecting some goal mashup");
    }
    if (minDepth < MIN_DEPTH) {
      throw new IllegalArgumentException("expecting positive minimum depth");
    }
    if (minDepth > maxDepth) {
      throw new IllegalArgumentException("expecting minimum depth to be less than or equal to maximum depth");
    }
    this.goalMashup = goalMashup;
    this.minDepth = minDepth;
    this.maxDepth = maxDepth;
  }

  public Mashup getGoalMashup() {
    return goalMashup;
  }

  public int getMinDepth() {
    return minDepth;
  }

  public int getMaxDepth() {
    return maxDepth;
  }

}
