/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.service.Service;

/**
 * @author Erik Wienhold
 */
public class PlannerService {

  private static final PlannerService INSTANCE = new PlannerService();

  private PlannerService() {
  }

  public static PlannerService getInstance() {
    return INSTANCE;
  }

  public PlannerJob createJob(final PlannerRequest request) {
    return new PlannerJob(request, Service.getInstance().getRepository());
  }

}
