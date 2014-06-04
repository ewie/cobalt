/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.HashSet;
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
    extends ComposingProvisionProvider<Property, PropertyProvision>
    implements PropertyProvisionProvider {

  private final Repository repository;

  public ComposingPropertyProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  protected Set<PropertyProvision> findProvisionsFor(final Property request) {
    return createProvisions(request, repository.findCompatibleProperties(request));
  }

  @Override
  protected PropertyProvision createProvision(final Property request, final Property offer, final Action action) {
    return new PropertyProvision(request, offer, action);
  }

  @Override
  protected Set<Property> getOffers(final Action action) {
    return action.getPublishedProperties();
  }


  private static Set<PropertyProvision> createProvisions(final Property request,
                                                         final Set<PublishedProperty> publishedProperties) {
    final Set<PropertyProvision> provisions = new HashSet<>(publishedProperties.size());
    for (final PublishedProperty pp : publishedProperties) {
      provisions.add(createProvision(request, pp));
    }
    return provisions;
  }

  private static PropertyProvision createProvision(final Property request, final PublishedProperty published) {
    return new PropertyProvision(request, published.getProperty(), published.getAction());
  }

}
