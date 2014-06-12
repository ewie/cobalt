/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.finders;

import java.util.Objects;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Type;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.utils.CappedLinkedHashMap;
import vsr.cobalt.repository.semantic.utils.ShortestPathFinder;

/**
 * Finds the distance between a request and its offer.
 *
 * @author Erik Wienhold
 */
public class RequestOfferDistanceFinder {

  public static final int DEFAULT_CACHE_SIZE = 32;

  private final Dataset dataset;

  private final CappedLinkedHashMap<Pair, Integer> cache;

  public RequestOfferDistanceFinder(final Dataset dataset, final int cacheSize) {
    this.dataset = dataset;
    cache = new CappedLinkedHashMap<>(cacheSize);
  }

  public RequestOfferDistanceFinder(final Dataset dataset) {
    this(dataset, DEFAULT_CACHE_SIZE);
  }

  public int getDistance(final Task request, final Task offer) {
    final Resource source = asResource(offer);
    final Resource target = asResource(request);
    return getDistance(source, target, Ontology.subTaskOf);
  }

  public int getDistance(final Type request, final Type offer) {
    final Resource source = asResource(offer);
    final Resource target = asResource(request);
    return getDistance(source, target, RDFS.subClassOf);
  }

  private int getDistance(final Resource source, final Resource target, final Property property) {
    final Pair p = new Pair(source, target);
    Integer distance = cache.get(p);
    if (distance == null) {
      distance = findDistance(source, target, property);
      cache.put(p, distance);
    }
    return distance;
  }

  private int findDistance(final Resource source, final Resource target, final Property property) {
    dataset.begin(ReadWrite.READ);
    try {
      final Model model = dataset.getDefaultModel();
      final ShortestPathFinder finder = new ShortestPathFinder(model, property);
      return finder.findShortestPathLength(source, target);
    } finally {
      dataset.end();
    }
  }

  private static Resource asResource(final Task task) {
    return ResourceFactory.createResource(task.getIdentifier());
  }

  private static Resource asResource(final Type type) {
    return ResourceFactory.createResource(type.getIdentifier());
  }

  private static class Pair {

    private final Resource source;

    private final Resource target;

    public Pair(final Resource source, final Resource target) {
      this.source = source;
      this.target = target;
    }

    @Override
    public int hashCode() {
      return Objects.hash(source, target);
    }

    @Override
    public boolean equals(final Object other) {
      return super.equals(other)
          || other instanceof Pair
          && equals((Pair) other);
    }

    private boolean equals(final Pair other) {
      return source.equals(other.source)
          && target.equals(other.target);
    }

  }

}
