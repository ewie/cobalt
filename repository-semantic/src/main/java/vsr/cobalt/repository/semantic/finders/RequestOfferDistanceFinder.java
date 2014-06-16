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
import com.hp.hpl.jena.vocabulary.RDFS;
import vsr.cobalt.models.Identifiable;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Type;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.externalizer.IdentifiableExternalizer;
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

  private final CappedLinkedHashMap<Pair<?>, Integer> cache;

  public RequestOfferDistanceFinder(final Dataset dataset, final int cacheSize) {
    this.dataset = dataset;
    cache = new CappedLinkedHashMap<>(cacheSize);
  }

  public RequestOfferDistanceFinder(final Dataset dataset) {
    this(dataset, DEFAULT_CACHE_SIZE);
  }

  public int getDistance(final Task request, final Task offer) {
    return getDistance(request, offer, Ontology.subTaskOf);
  }

  public int getDistance(final Type request, final Type offer) {
    return getDistance(request, offer, RDFS.subClassOf);
  }

  private <T extends Identifiable> int getDistance(final T request, final T offer, final Property property) {
    final Pair<T> p = new Pair<>(request, offer);
    Integer distance = cache.get(p);
    if (distance == null) {
      distance = findDistance(request, offer, property);
      cache.put(p, distance);
    }
    return distance;
  }

  private <T extends Identifiable> int findDistance(final T request, final T offer, final Property property) {
    dataset.begin(ReadWrite.READ);
    try {
      final Model model = dataset.getDefaultModel();
      final Resource source = asResource(offer, model);
      final Resource target = asResource(request, model);
      final ShortestPathFinder finder = new ShortestPathFinder(model, property);
      return finder.findShortestPathLength(source, target);
    } finally {
      dataset.end();
    }
  }

  private static Resource asResource(final Identifiable task, final Model model) {
    return IdentifiableExternalizer.externalize(task, model);
  }

  private static class Pair<T> {

    private final T source;

    private final T target;

    public Pair(final T source, final T target) {
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
