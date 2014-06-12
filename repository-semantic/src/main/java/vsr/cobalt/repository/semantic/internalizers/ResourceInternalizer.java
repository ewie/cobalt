/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers;

import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Translates a {@link Resource} to a domain model.
 *
 * @author Erik Wienhold
 */
public interface ResourceInternalizer<T> {

  /**
   * Internalize a single resource.
   *
   * @param resource the resource to internalize
   *
   * @return a domain model
   */
  T internalize(Resource resource);

  /**
   * Internalize multiple resources given as an iterable.
   *
   * @param resources the resources to internalize
   *
   * @return a set of domain models
   */
  Set<T> internalizeAll(Iterable<Resource> resources);

  /**
   * Internalize multiple resources given as an iterator.
   *
   * @param resources the resource to internalize
   *
   * @return a set of domain models
   */
  Set<T> internalizeAll(Iterator<Resource> resources);

}
