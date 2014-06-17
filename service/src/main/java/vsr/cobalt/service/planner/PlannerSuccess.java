/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.planner.collectors.rating.RatedPlan;

/**
 * @author Erik Wienhold
 */
public final class PlannerSuccess implements PlannerResponse {

  private final Iterable<RatedPlan> plans;

  public PlannerSuccess(final Iterable<RatedPlan> plans) {
    this.plans = plans;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public Iterable<RatedPlan> getPlans() {
    return plans;
  }

  @Override
  public Exception getFailure() {
    return null;
  }

}
