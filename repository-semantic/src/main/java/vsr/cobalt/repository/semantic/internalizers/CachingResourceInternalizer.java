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
import vsr.cobalt.repository.semantic.utils.CappedLinkedHashMap;
import vsr.cobalt.repository.semantic.utils.NodeToResourceMapper;

/**
 * An internalizer which caches a certain number of resources and their corresponding models.
 *
 * @author Erik Wienhold
 */
public abstract class CachingResourceInternalizer<T> implements ResourceInternalizer<T> {

  public static final int DEFAULT_CACHE_SIZE = 32;

  private final CappedLinkedHashMap<Resource, T> cache;

  public CachingResourceInternalizer(final int cacheSize) {
    cache = new CappedLinkedHashMap<>(cacheSize);
  }

  public CachingResourceInternalizer() {
    this(DEFAULT_CACHE_SIZE);
  }

  @Override
  public T internalize(final Resource resource) {
    if (!cache.containsKey(resource)) {
      final T x = create(resource);
      cache.put(resource, x);
    }
    return cache.get(resource);
  }

  @Override
  public Set<T> internalizeAll(final Iterable<Resource> resources) {
    return internalizeAll(resources.iterator());
  }

  @Override
  public Set<T> internalizeAll(final Iterator<Resource> resources) {
    final Set<T> s = new HashSet<>();
    while (resources.hasNext()) {
      s.add(internalize(resources.next()));
    }
    return s;
  }

  protected abstract T create(Resource resource);

  protected Iterator<Resource> getResourceObjects(final Resource s, final Property p) {
    return new NodeToResourceMapper(s.getModel().listObjectsOfProperty(s, p));
  }

  protected Resource getResourceObject(final Resource s, final Property p) {
    final Iterator<Resource> resources = getResourceObjects(s, p);
    if (resources.hasNext()) {
      return resources.next();
    }
    return null;
  }

  protected Resource getSubject(final Resource o, final Property p) {
    final ResIterator rs = o.getModel().listResourcesWithProperty(p, o);
    if (rs.hasNext()) {
      return rs.next();
    }
    return null;
  }

  protected Literal getLiteralObject(final Resource s, final Property p) {
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
