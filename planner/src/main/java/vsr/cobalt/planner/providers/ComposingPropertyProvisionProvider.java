/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

import java.util.Set;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;
import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.graph.PropertyProvision;

/**
 * @author Erik Wienhold
 */
public class ComposingPropertyProvisionProvider
    extends ComposingProvisionProvider<Property, PublishedProperty, PropertyProvision>
    implements PropertyProvisionProvider {

  private final Repository repository;

  public ComposingPropertyProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  protected Set<PublishedProperty> getOffersFor(final Property request) {
    return repository.findCompatibleProperties(request);
  }

  @Override
  protected PropertyProvision createProvision(final Property request, final Property subject, final Action action) {
    return new PropertyProvision(request, new PublishedProperty(subject, action));
  }

  @Override
  protected Set<Property> getOfferedSubjects(final Action action) {
    return action.getPublishedProperties();
  }

}
