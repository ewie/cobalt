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
public class PlannerFailure implements PlannerResponse {

  private final Exception failure;

  public PlannerFailure(final Exception failure) {
    this.failure = failure;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

  @Override
  public Iterable<RatedPlan> getPlans() {
    return null;
  }

  @Override
  public Exception getFailure() {
    return failure;
  }

}
