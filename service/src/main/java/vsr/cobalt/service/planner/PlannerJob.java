/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.GraphExtender;
import vsr.cobalt.planner.GraphFactory;
import vsr.cobalt.planner.PlanCollector;
import vsr.cobalt.planner.PlanExtractor;
import vsr.cobalt.planner.PlanningException;
import vsr.cobalt.planner.PlanningTask;
import vsr.cobalt.planner.collectors.RatingPlanCollector;
import vsr.cobalt.planner.extenders.DefaultGraphExtender;
import vsr.cobalt.planner.extenders.DefaultGraphFactory;
import vsr.cobalt.planner.extenders.PathWalkingCyclicDependencyDetector;
import vsr.cobalt.planner.extractors.BackwardChainingPlanExtractor;
import vsr.cobalt.planner.providers.BasicPrecursorActionProvider;
import vsr.cobalt.planner.providers.ComposingExtendedPrecursorActionProvider;
import vsr.cobalt.planner.providers.ComposingFunctionalityProvisionProvider;
import vsr.cobalt.planner.providers.ComposingPropertyProvisionProvider;

/**
 * @author Erik Wienhold
 */
public class PlannerJob {

  private final PlannerRequest request;

  private final Repository repository;

  public PlannerJob(final PlannerRequest request, final Repository repository) {
    this.request = request;
    this.repository = repository;
  }

  public PlannerResponse run() {
    final RatingPlanCollector plans = createPlanCollector();
    final PlanningTask planner = createPlanningTask(plans);

    while (!planner.isDone()) {
      try {
        planner.advance();
      } catch (final PlanningException ex) {
        return new PlannerFailure(ex);
      }
    }

    return new PlannerSuccess(plans);
  }

  private RatingPlanCollector createPlanCollector() {
    return new RatingPlanCollector(new DefaultPlanRater(repository));
  }

  private PlanningTask createPlanningTask(final PlanCollector collector) {
    final GraphFactory gf = new DefaultGraphFactory(new ComposingFunctionalityProvisionProvider(repository));
    final GraphExtender gx = new DefaultGraphExtender(
        new ComposingExtendedPrecursorActionProvider(repository,
            new BasicPrecursorActionProvider(repository)),
        new ComposingPropertyProvisionProvider(repository),
        new PathWalkingCyclicDependencyDetector());
    final PlanExtractor px = new BackwardChainingPlanExtractor();
    return new PlanningTask(request.getPlanningProblem(), gf, gx, px, collector);
  }

}
