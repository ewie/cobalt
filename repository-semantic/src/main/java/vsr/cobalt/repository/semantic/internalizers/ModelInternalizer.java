/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers;

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Translates a {@link Resource} to a domain model.
 *
 * @author Erik Wienhold
 */
public interface ModelInternalizer<T> {

  /**
   * Internalize a resources found in a model.
   *
   * @param model a model containing resources to internalize
   *
   * @return a set of domain models
   */
  Set<T> internalize(Model model);

}
