/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Type;
import vsr.cobalt.repository.semantic.internalizers.CachingResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class CachingTypeResourceInternalizer extends CachingResourceInternalizer<Type> {

  @Override
  protected Type create(final Resource r) {
    return new Type(r.getURI());
  }

}
