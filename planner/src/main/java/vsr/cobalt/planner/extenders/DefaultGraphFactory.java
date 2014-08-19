/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders;

import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.planner.GraphFactory;
import vsr.cobalt.planner.PlanningException;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.providers.FunctionalityProvisionProvider;

/**
 * The default graph factory creates an initial graph from a mashup description by discovering and selecting actions
 * realizing some required functionality.
 *
 * @author Erik Wienhold
 */
public class DefaultGraphFactory implements GraphFactory {

  private final FunctionalityProvisionProvider functionalityProvisionProvider;

  /**
   * @param functionalityProvisionProvider a provider of functionality provisions
   */
  public DefaultGraphFactory(final FunctionalityProvisionProvider functionalityProvisionProvider) {
    this.functionalityProvisionProvider = functionalityProvisionProvider;
  }

  @Override
  public Graph createGraph(final Mashup mashup) throws PlanningException {
    final Set<Functionality> fs = mashup.getFunctionalities();

    final Set<FunctionalityProvision> fps = functionalityProvisionProvider.getProvisionsFor(fs);

    if (fps.isEmpty() || !satisfyAllFunctionalities(fps, fs)) {
      throw new PlanningException("cannot realize all mashup functionalities");
    }

    return Graph.create(new InitialLevel(fps));
  }

  /**
   * Check if a set of functionality provisions satisfy all functionalities.
   *
   * @param provisions      a set of functionality provisions
   * @param functionalities a set of requested functionalities
   *
   * @return true when all functionalities are satisfied, false otherwise
   */
  private boolean satisfyAllFunctionalities(final Set<FunctionalityProvision> provisions,
                                            final Set<Functionality> functionalities) {
    final Set<Functionality> satisfiedFunctionalities = new HashSet<>();

    for (final FunctionalityProvision fp : provisions) {
      satisfiedFunctionalities.add(fp.getRequest());
    }

    return satisfiedFunctionalities.equals(functionalities);
  }

}
