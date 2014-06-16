/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Type;

/**
 * @author Erik Wienhold
 */
public class TypeResourceInternalizer extends IdentifiableResourceInternalizer<Type> {

  @Override
  public Type internalize(final Resource r) {
    return new Type(asIdentifier(r));
  }

}
