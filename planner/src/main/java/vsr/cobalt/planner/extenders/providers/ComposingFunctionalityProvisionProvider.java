/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders.providers;

import java.util.Set;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.RealizedFunctionality;
import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.graph.FunctionalityProvision;

/**
 * @author Erik Wienhold
 */
public class ComposingFunctionalityProvisionProvider
    extends ComposingProvisionProvider<Functionality, RealizedFunctionality, FunctionalityProvision>
    implements FunctionalityProvisionProvider {

  private final Repository repository;

  public ComposingFunctionalityProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  protected Set<RealizedFunctionality> getOffersFor(final Functionality functionality) {
    return repository.findCompatibleOffers(functionality);
  }

  @Override
  protected FunctionalityProvision createProvision(final Functionality request, final Functionality subject,
                                                   final Action action) {
    return new FunctionalityProvision(request, new RealizedFunctionality(subject, action));
  }

  @Override
  protected Set<Functionality> getOfferedSubjects(final Action action) {
    return action.getRealizedFunctionalities();
  }

}
