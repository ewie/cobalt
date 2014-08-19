/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.RealizedFunctionality;
import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.graph.FunctionalityProvision;

/**
 * A functionality provision provider which simply delegates every functionality request to a repository and collects
 * all found provisions.
 *
 * @author Erik Wienhold
 */
public class BasicFunctionalityProvisionProvider implements FunctionalityProvisionProvider {

  private final Repository repository;

  /**
   * @param repository the repository to delegate to
   */
  public BasicFunctionalityProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  public Set<FunctionalityProvision> getProvisionsFor(final Set<Functionality> functionalities) {
    final Set<FunctionalityProvision> tps = new HashSet<>();
    for (final Functionality f : functionalities) {
      tps.addAll(createProvisions(f, repository.findCompatibleOffers(f)));
    }
    return tps;
  }

  private static Collection<FunctionalityProvision> createProvisions(final Functionality request,
                                                                     final Collection<RealizedFunctionality>
                                                                         realizedFunctionalities) {
    final Collection<FunctionalityProvision> provisions = new ArrayList<>(realizedFunctionalities.size());
    for (final RealizedFunctionality rf : realizedFunctionalities) {
      provisions.add(new FunctionalityProvision(request, rf));
    }
    return provisions;
  }

}
