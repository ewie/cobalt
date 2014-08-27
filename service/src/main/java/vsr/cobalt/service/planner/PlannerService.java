/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.planner.Repository;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.repository.semantic.SemanticRepository;
import vsr.cobalt.service.Service;
import vsr.cobalt.service.planner.distance.FunctionalityProvisionDistanceMeter;
import vsr.cobalt.service.planner.distance.PropertyProvisionDistanceMeter;
import vsr.cobalt.service.planner.distance.ProvisionDistanceMeter;

/**
 * @author Erik Wienhold
 */
public class PlannerService {

  private static final PlannerService INSTANCE = new PlannerService();

  private Repository repository;

  private PlannerService() {
  }

  public static PlannerService getInstance() {
    return INSTANCE;
  }

  public PlannerJob createJob(final PlannerRequest request) {
    return new PlannerJob(request, getRepository());
  }

  public ProvisionDistanceMeter<PropertyProvision> getPropertyDistanceMeter() {
    return new PropertyProvisionDistanceMeter(getRepository());
  }

  public ProvisionDistanceMeter<FunctionalityProvision> getFunctionalityDistanceMeter() {
    return new FunctionalityProvisionDistanceMeter(getRepository());
  }

  private Repository getRepository() {
    if (repository == null) {
      repository = new SemanticRepository(Service.getInstance().getDataset());
    }
    return repository;
  }

}
