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

  private final Mashup mashup;

  private final int minDepth;

  private final int maxDepth;

  public PlannerRequest(final Mashup mashup, final int minDepth, final int maxDepth) {
    if (minDepth < MIN_DEPTH) {
      throw new IllegalArgumentException("expecting positive minimum depth");
    }
    if (minDepth > maxDepth) {
      throw new IllegalArgumentException("expecting minimum depth to be less than or equal to maximum depth");
    }
    this.mashup = mashup;
    this.minDepth = minDepth;
    this.maxDepth = maxDepth;
  }

  public Mashup getMashup() {
    return mashup;
  }

  public int getMinDepth() {
    return minDepth;
  }

  public int getMaxDepth() {
    return maxDepth;
  }

}
