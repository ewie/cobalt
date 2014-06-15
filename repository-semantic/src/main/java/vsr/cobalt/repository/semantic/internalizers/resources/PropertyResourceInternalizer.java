/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Type;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class PropertyResourceInternalizer extends ResourceInternalizer<Property> {

  private final ResourceInternalizer<Type> type;

  public PropertyResourceInternalizer(final ResourceInternalizer<Type> type) {
    this.type = type;
  }

  @Override
  public Property internalize(final Resource r) {
    final String name = getLiteralObject(r, Ontology.hasName).getString();
    final Type type = this.type.internalize(getResourceObject(r, Ontology.hasType));
    return new Property(name, type);
  }

}
