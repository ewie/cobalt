/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.repository.semantic.utils.NodeToResourceMapper;

/**
 * Translates a {@link Resource} to a domain model.
 *
 * @author Erik Wienhold
 */
public abstract class ResourceInternalizer<T> {

  /**
   * Internalize a single resource.
   *
   * @param resource the resource to internalize
   *
   * @return a domain model
   */
  abstract public T internalize(Resource resource);

  /**
   * Internalize multiple resources given as an iterable.
   *
   * @param resources the resources to internalize
   *
   * @return a set of domain models
   */
  public Set<T> internalizeAll(final Iterable<Resource> resources) {
    return internalizeAll(resources.iterator());
  }

  /**
   * Internalize multiple resources given as an iterator.
   *
   * @param resources the resource to internalize
   *
   * @return a set of domain models
   */
  public Set<T> internalizeAll(final Iterator<Resource> resources) {
    final Set<T> set = new HashSet<>();
    while (resources.hasNext()) {
      set.add(internalize(resources.next()));
    }
    return set;
  }

  protected static Iterator<Resource> getResourceObjects(final Resource s, final Property p) {
    return new NodeToResourceMapper(s.getModel().listObjectsOfProperty(s, p));
  }

  protected static Resource getResourceObject(final Resource s, final Property p) {
    final Iterator<Resource> resources = getResourceObjects(s, p);
    if (resources.hasNext()) {
      return resources.next();
    }
    return null;
  }

  protected static Resource getSubject(final Resource o, final Property p) {
    final ResIterator rs = o.getModel().listResourcesWithProperty(p, o);
    if (rs.hasNext()) {
      return rs.next();
    }
    return null;
  }

  protected static Literal getLiteralObject(final Resource s, final Property p) {
    final NodeIterator nodes = s.getModel().listObjectsOfProperty(s, p);
    if (nodes.hasNext()) {
      final RDFNode node = nodes.next();
      if (node.isLiteral()) {
        return node.asLiteral();
      }
    }
    return null;
  }

}
