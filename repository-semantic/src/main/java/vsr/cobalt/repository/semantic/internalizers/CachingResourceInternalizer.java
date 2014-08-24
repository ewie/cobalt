/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.repository.semantic.utils.CappedLinkedHashMap;

/**
 * An internalizer which caches a certain number of resources and their corresponding models.
 *
 * @author Erik Wienhold
 */
public class CachingResourceInternalizer<T> extends ResourceInternalizer<T> {

  public static final int DEFAULT_CACHE_SIZE = 32;

  private final ResourceInternalizer<T> internalizer;

  private final CappedLinkedHashMap<Resource, T> cache;

  public CachingResourceInternalizer(final ResourceInternalizer<T> internalizer, final int cacheSize) {
    this.internalizer = internalizer;
    cache = new CappedLinkedHashMap<>(cacheSize);
  }

  public CachingResourceInternalizer(final ResourceInternalizer<T> internalizer) {
    this(internalizer, DEFAULT_CACHE_SIZE);
  }

  @Override
  public T internalize(final Resource resource) {
    T intern = cache.get(resource);
    if (intern == null) {
      intern = internalizer.internalize(resource);
      cache.put(resource, intern);
    }
    return intern;
  }

  public void clearCache() {
    cache.clear();
  }

}
