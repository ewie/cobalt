/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.DefaultMashupPlanner;
import vsr.cobalt.planner.GraphExtender;
import vsr.cobalt.planner.GraphFactory;
import vsr.cobalt.planner.PlanCollector;
import vsr.cobalt.planner.PlanExtractor;
import vsr.cobalt.planner.PlanningException;
import vsr.cobalt.planner.PlanningProcess;
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
    final PlanningProcess process = createPlanningTask(createPlanner(), plans);

    while (!process.isDone()) {
      try {
        process.advance();
      } catch (final PlanningException ex) {
        return new PlannerFailure(ex);
      }
    }

    return new PlannerSuccess(plans);
  }

  private RatingPlanCollector createPlanCollector() {
    return new RatingPlanCollector(new DefaultPlanRater(repository));
  }

  private PlanningProcess createPlanningTask(final DefaultMashupPlanner planner, final PlanCollector collector) {
    return planner.createPlanningProcess(request.getPlanningProblem(), collector);
  }

  private DefaultMashupPlanner createPlanner() {
    return new DefaultMashupPlanner(createGraphFactory(), createGraphExtender(), createPlanExtractor());
  }

  private GraphFactory createGraphFactory() {
    return new DefaultGraphFactory(new ComposingFunctionalityProvisionProvider(repository));
  }

  private GraphExtender createGraphExtender() {
    return new DefaultGraphExtender(
        new ComposingExtendedPrecursorActionProvider(repository,
            new BasicPrecursorActionProvider(repository)),
        new ComposingPropertyProvisionProvider(repository),
        new PathWalkingCyclicDependencyDetector());
  }

  private PlanExtractor createPlanExtractor() {
    return new BackwardChainingPlanExtractor();
  }

}
