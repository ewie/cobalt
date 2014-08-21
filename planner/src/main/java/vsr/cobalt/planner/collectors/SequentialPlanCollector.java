/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors;

import java.util.ArrayDeque;

import vsr.cobalt.planner.Plan;

/**
 * A queue-based plan collector which simply queues collected plans.
 *
 * @author Erik Wienhold
 */
public class SequentialPlanCollector extends QueueingPlanCollector<Plan> {

  public SequentialPlanCollector() {
    super(new ArrayDeque<Plan>());
  }

  @Override
  public Result collect(final Plan plan) {
    offer(plan);
    return Result.CONTINUE;
  }

}
