/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class InteractionResourceInternalizer extends ResourceInternalizer<Interaction> {

  @Override
  public Interaction internalize(final Resource r) {
    final String instruction = getLiteralObject(r, Ontology.hasInstruction).getString();
    return new Interaction(instruction);
  }

}
