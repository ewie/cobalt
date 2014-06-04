/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.planner.graph.PropertyProvision;

/**
 * @author Erik Wienhold
 */
public class ComposingPropertyProvisionProvider
    extends ComposingProvisionProvider<Property, PropertyProvision>
    implements PropertyProvisionProvider {

  private final Repository repository;

  public ComposingPropertyProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  protected Set<PropertyProvision> findProvisionsFor(final Property request) {
    return repository.findCompatibleProperties(request);
  }

  @Override
  protected PropertyProvision createProvision(final Property request, final Property offer, final Action action) {
    return new PropertyProvision(request, offer, action);
  }

  @Override
  protected Set<Property> getOffers(final Action action) {
    return action.getPublishedProperties();
  }

}
