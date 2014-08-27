/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.planner.Plan;

/**
 * @author Erik Wienhold
 */
public final class PlannerSuccess implements PlannerResponse {

  private final Iterable<Plan> plans;

  public PlannerSuccess(final Iterable<Plan> plans) {
    this.plans = plans;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public Iterable<Plan> getPlans() {
    return plans;
  }

  @Override
  public Throwable getCause() {
    return null;
  }

}
