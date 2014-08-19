/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.models.Mashup;
import vsr.cobalt.planner.PlanningProblem;

/**
 * @author Erik Wienhold
 */
public final class PlannerRequest {

  public static final int MIN_DEPTH = PlanningProblem.MIN_DEPTH;

  public static final int MAX_DEPTH = PlanningProblem.MAX_DEPTH;

  private final PlanningProblem problem;

  public PlannerRequest(final Mashup goalMashup, final int minDepth, final int maxDepth) {
    if (goalMashup == null) {
      throw new IllegalArgumentException("expecting some goal mashup");
    }
    problem = new PlanningProblem(goalMashup, minDepth, maxDepth);
  }

  public PlanningProblem getPlanningProblem() {
    return problem;
  }

}
