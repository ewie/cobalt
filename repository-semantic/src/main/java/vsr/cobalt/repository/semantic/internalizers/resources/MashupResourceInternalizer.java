/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.models.Task;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class MashupResourceInternalizer extends ResourceInternalizer<Mashup> {

  private final ResourceInternalizer<Task> tasks;

  public MashupResourceInternalizer(final ResourceInternalizer<Task> tasks) {
    this.tasks = tasks;
  }

  @Override
  public Mashup internalize(final Resource r) {
    final Set<Task> ts = tasks.internalizeAll(getResourceObjects(r, Ontology.realizesTask));
    return new Mashup(ts);
  }

}
