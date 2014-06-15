/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Type;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class TypeResourceInternalizer extends ResourceInternalizer<Type> {

  @Override
  public Type internalize(final Resource r) {
    return new Type(r.getURI());
  }

}
