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
import vsr.cobalt.models.PropositionSet;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.CachingResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class CachingPropositionsResourceInternalizer extends CachingResourceInternalizer<PropositionSet> {

  private final ResourceInternalizer<Property> properties;

  public CachingPropositionsResourceInternalizer(final ResourceInternalizer<Property> properties) {
    this.properties = properties;
  }

  @Override
  protected PropositionSet create(final Resource r) {
    if (r == null) {
      return PropositionSet.empty();
    }
    final Set<Property> c = internalize(r, Ontology.clearValueOf);
    final Set<Property> f = internalize(r, Ontology.fillValueOf);
    return new PropositionSet(c, f);
  }

  private Set<Property> internalize(final Resource s, final com.hp.hpl.jena.rdf.model.Property p) {
    return properties.internalizeAll(getResourceObjects(s, p));
  }

}
