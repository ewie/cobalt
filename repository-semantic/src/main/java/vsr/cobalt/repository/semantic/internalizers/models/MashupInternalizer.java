/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.models;

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.CachingResourceInternalizers;
import vsr.cobalt.repository.semantic.internalizers.ModelInternalizer;

/**
 * @author Erik Wienhold
 */
public class MashupInternalizer implements ModelInternalizer<Mashup> {

  @Override
  public Set<Mashup> internalize(final Model model) {
    final ResIterator resources = model.listSubjectsWithProperty(RDF.type, Ontology.Mashup);
    return CachingResourceInternalizers.mashups.internalizeAll(resources);
  }

}
