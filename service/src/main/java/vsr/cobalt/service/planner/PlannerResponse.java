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
public interface PlannerResponse {

  boolean isSuccess();

  Iterable<RatedPlan> getPlans();

  Throwable getCause();

}
