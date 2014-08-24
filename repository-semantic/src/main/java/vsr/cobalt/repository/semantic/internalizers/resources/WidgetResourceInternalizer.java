/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class WidgetResourceInternalizer extends IdentifiableResourceInternalizer<Widget> {

  private final ResourceInternalizer<Property> properties;

  public WidgetResourceInternalizer(final ResourceInternalizer<Property> properties) {
    this.properties = properties;
  }

  @Override
  public Widget internalize(final Resource r) {
    return new Widget(asIdentifier(r), internalizePublishedProperties(r));
  }

  private Set<Property> internalizePublishedProperties(final Resource r) {
    return properties.internalizeAll(getResourceObjects(r, Ontology.hasPublicProperty));
  }

}
