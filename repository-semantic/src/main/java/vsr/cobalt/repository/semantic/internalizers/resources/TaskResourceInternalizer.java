/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Task;

/**
 * @author Erik Wienhold
 */
public class TaskResourceInternalizer extends IdentifiableResourceInternalizer<Task> {

  @Override
  public Task internalize(final Resource r) {
    return new Task(asIdentifier(r));
  }

}
