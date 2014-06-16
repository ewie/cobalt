/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.externalizer;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Identifiable;
import vsr.cobalt.models.Identifier;

/**
 * @author Erik Wienhold
 */
public abstract class IdentifiableExternalizer {

  public static Resource externalize(final Identifiable identifiable, final Model model) {
    final Identifier id = identifiable.getIdentifier();
    if (id.isUri()) {
      return model.createResource(id.toString());
    } else {
      return model.createResource(AnonId.create(id.toString()));
    }
  }

}
