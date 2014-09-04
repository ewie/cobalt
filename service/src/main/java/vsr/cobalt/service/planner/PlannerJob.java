/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import java.util.ArrayList;
import java.util.Collection;

import vsr.cobalt.planner.DefaultMashupPlanner;
import vsr.cobalt.planner.GraphExtender;
import vsr.cobalt.planner.GraphFactory;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.PlanCollector;
import vsr.cobalt.planner.PlanExtractor;
import vsr.cobalt.planner.PlanningProcess;
import vsr.cobalt.planner.Repository;
import vsr.cobalt.planner.extenders.DefaultGraphExtender;
import vsr.cobalt.planner.extenders.DefaultGraphFactory;
import vsr.cobalt.planner.extenders.PathWalkingCyclicDependencyDetector;
import vsr.cobalt.planner.extenders.providers.BasicFunctionalityProvisionProvider;
import vsr.cobalt.planner.extenders.providers.BasicPrecursorActionProvider;
import vsr.cobalt.planner.extenders.providers.BasicPropertyProvisionProvider;
import vsr.cobalt.planner.extenders.providers.ComposingExtendedPrecursorActionProvider;
import vsr.cobalt.planner.extenders.providers.ComposingFunctionalityProvisionProvider;
import vsr.cobalt.planner.extenders.providers.ComposingMinimalPrecursorActionProvider;
import vsr.cobalt.planner.extenders.providers.ComposingPropertyProvisionProvider;
import vsr.cobalt.planner.extenders.providers.FunctionalityProvisionProvider;
import vsr.cobalt.planner.extenders.providers.PrecursorActionProvider;
import vsr.cobalt.planner.extenders.providers.PropertyProvisionProvider;
import vsr.cobalt.planner.extractors.BackwardChainingPlanExtractor;

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
    final Collection<Plan> plans = new ArrayList<>();
    final PlanningProcess process = createPlanningTask(createPlanner(), createPlanCollector(plans));

    while (!process.isDone()) {
      try {
        process.advance();
      } catch (final Exception ex) {
        // it's only a failure when there are no plans
        if (plans.isEmpty()) {
          return new PlannerFailure(ex);
        }
      }
    }

    return new PlannerSuccess(plans);
  }

  private PlanCollector createPlanCollector(final Collection<Plan> plans) {
    return new PlanCollector() {
      @Override
      public Result collect(final Plan plan) {
        plans.add(plan);
        return Result.CONTINUE;
      }
    };
  }

  private PlanningProcess createPlanningTask(final DefaultMashupPlanner planner, final PlanCollector collector) {
    return planner.createPlanningProcess(request.getPlanningProblem(), collector);
  }

  private DefaultMashupPlanner createPlanner() {
    return new DefaultMashupPlanner(createGraphFactory(), createGraphExtender(), createPlanExtractor());
  }

  private GraphFactory createGraphFactory() {
    return new DefaultGraphFactory(createFunctionalityProvisionProvider());
  }

  private GraphExtender createGraphExtender() {
    return new DefaultGraphExtender(
        createPrecursorActionProvider(),
        createPropertyProvisionProvider(),
        new PathWalkingCyclicDependencyDetector());
  }

  private FunctionalityProvisionProvider createFunctionalityProvisionProvider() {
    if (request.getActionCompositionStrategy().composeFunctionalityProviders()) {
      return new ComposingFunctionalityProvisionProvider(repository);
    } else {
      return new BasicFunctionalityProvisionProvider(repository);
    }
  }

  private PrecursorActionProvider createPrecursorActionProvider() {
    switch (request.getActionCompositionStrategy().getPrecursorCompositionStrategy()) {
    case NONE:
      return createBasicPrecursorActionProvider();
    case MINIMAL:
      return createComposingMinimalPrecursorActionProvider();
    case EXTENDED_ATOMIC:
      return createComposingExtendedPrecursorActionProvider(createBasicPrecursorActionProvider());
    case EXTENDED_MINIMAL:
      return createComposingExtendedPrecursorActionProvider(createComposingMinimalPrecursorActionProvider());
    default:
      throw new RuntimeException("unsupported precursor action composition strategy");
    }
  }

  private PropertyProvisionProvider createPropertyProvisionProvider() {
    if (request.getActionCompositionStrategy().composePropertyProviders()) {
      return new ComposingPropertyProvisionProvider(repository);
    } else {
      return new BasicPropertyProvisionProvider(repository);
    }
  }

  private BasicPrecursorActionProvider createBasicPrecursorActionProvider() {
    return new BasicPrecursorActionProvider(repository);
  }

  private ComposingMinimalPrecursorActionProvider createComposingMinimalPrecursorActionProvider() {
    return new ComposingMinimalPrecursorActionProvider(repository);
  }

  private ComposingExtendedPrecursorActionProvider
  createComposingExtendedPrecursorActionProvider(final PrecursorActionProvider provider) {
    return new ComposingExtendedPrecursorActionProvider(repository, provider);
  }

  private PlanExtractor createPlanExtractor() {
    return new BackwardChainingPlanExtractor();
  }

}
