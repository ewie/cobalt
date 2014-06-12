/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Task;
import vsr.cobalt.repository.semantic.internalizers.CachingResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class CachingTaskResourceInternalizer extends CachingResourceInternalizer<Task> {

  @Override
  protected Task create(final Resource r) {
    return new Task(r.getURI());
  }

}
