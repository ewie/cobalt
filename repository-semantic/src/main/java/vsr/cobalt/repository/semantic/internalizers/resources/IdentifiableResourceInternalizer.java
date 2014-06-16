/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import java.net.URI;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Identifiable;
import vsr.cobalt.models.Identifier;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public abstract class IdentifiableResourceInternalizer<T extends Identifiable> extends ResourceInternalizer<T> {

  public static Identifier asIdentifier(final Resource resource) {
    if (resource.isURIResource()) {
      return Identifier.create(URI.create(resource.getURI()));
    } else {
      return Identifier.create(resource.getId().getLabelString());
    }
  }

}
