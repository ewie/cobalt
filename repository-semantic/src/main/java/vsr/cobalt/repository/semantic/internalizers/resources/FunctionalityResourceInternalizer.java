/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Functionality;

/**
 * @author Erik Wienhold
 */
public class FunctionalityResourceInternalizer extends IdentifiableResourceInternalizer<Functionality> {

  @Override
  public Functionality internalize(final Resource r) {
    return new Functionality(asIdentifier(r));
  }

}
