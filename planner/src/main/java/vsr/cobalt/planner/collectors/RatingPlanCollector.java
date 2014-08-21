/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors;

import java.util.PriorityQueue;

import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.rating.PlanRater;
import vsr.cobalt.planner.rating.RatedPlan;
import vsr.cobalt.planner.rating.Rating;

/**
 * A queue-based plan collector which queues collected plans according to the natural order of their rating.
 *
 * @author Erik Wienhold
 */
public class RatingPlanCollector extends QueueingPlanCollector<RatedPlan> {

  private final PlanRater rater;

  /**
   * @param rater a plan rater
   */
  public RatingPlanCollector(final PlanRater rater) {
    super(new PriorityQueue<RatedPlan>());
    this.rater = rater;
  }

  @Override
  public Result collect(final Plan plan) {
    final Rating rating = rater.rate(plan);
    if (rating != null) {
      offer(new RatedPlan(plan, rating));
    }
    return Result.CONTINUE;
  }

}
