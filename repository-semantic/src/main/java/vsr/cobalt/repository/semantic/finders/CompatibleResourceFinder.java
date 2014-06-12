/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.finders;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Offer;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;
import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Task;
import vsr.cobalt.repository.semantic.internalizers.CachingResourceInternalizers;
import vsr.cobalt.repository.semantic.utils.CappedLinkedHashMap;
import vsr.cobalt.repository.semantic.utils.ResourceCache;

/**
 * @author Erik Wienhold
 */
public class CompatibleResourceFinder {

  public static final int DEFAULT_CACHE_SIZE = 32;

  private final TaskFinder taskFinder;

  private final PropertyFinder propertyFinder;

  public CompatibleResourceFinder(final Dataset dataset, final int cacheSize) {
    propertyFinder = new PropertyFinder(dataset, cacheSize);
    taskFinder = new TaskFinder(dataset, cacheSize);
  }

  public CompatibleResourceFinder(final Dataset dataset) {
    this(dataset, DEFAULT_CACHE_SIZE);
  }

  public Set<RealizedTask> findCompatibleTasks(final Task task) {
    return taskFinder.findCompatibleResources(task);
  }

  public Set<PublishedProperty> findCompatibleProperties(final Property property) {
    return propertyFinder.findCompatibleResources(property);
  }

  private static abstract class Finder<T, O extends Offer<T>> {

    private final Dataset dataset;

    private final CappedLinkedHashMap<T, Set<O>> cache;

    public Finder(final Dataset dataset, final int cacheSize) {
      this.dataset = dataset;
      cache = new CappedLinkedHashMap<>(cacheSize);
    }

    public Finder(final Dataset dataset) {
      this(dataset, DEFAULT_CACHE_SIZE);
    }

    protected abstract String getQuery(T request);

    protected abstract O createOffer(QuerySolution solution);

    public Set<O> findCompatibleResources(final T request) {
      Set<O> offers = cache.get(request);

      if (offers == null) {
        offers = findOffers(request);
        cache.put(request, offers);
      }

      return offers;
    }

    private Set<O> findOffers(final T request) {
      final Set<O> offers = new HashSet<>();

      dataset.begin(ReadWrite.READ);

      try {
        final QueryExecution qx = QueryExecutionFactory.create(getQuery(request), dataset);

        try {
          final ResultSet rs = qx.execSelect();
          while (rs.hasNext()) {
            offers.add(createOffer(rs.next()));
          }
        } finally {
          qx.close();
        }
      } finally {
        dataset.end();
      }
      return offers;
    }

  }

  private static class TaskFinder extends Finder<Task, RealizedTask> {

    public TaskFinder(final Dataset dataset, final int cacheSize) {
      super(dataset, cacheSize);
    }

    @Override
    protected String getQuery(final Task request) {
      final ParameterizedSparqlString pss = new ParameterizedSparqlString(
          ResourceCache.getInstance().get("/sparql/compatible-tasks.rq"));
      pss.setIri("request", request.getIdentifier());
      return pss.toString();
    }

    @Override
    protected RealizedTask createOffer(final QuerySolution solution) {
      final Task offer = CachingResourceInternalizers.tasks.internalize(solution.getResource("offer"));
      final Action action = CachingResourceInternalizers.actions.internalize(solution.getResource("action"));
      return new RealizedTask(offer, action);
    }

  }

  private static class PropertyFinder extends Finder<Property, PublishedProperty> {

    public PropertyFinder(final Dataset dataset, final int cacheSize) {
      super(dataset, cacheSize);
    }

    @Override
    protected String getQuery(final Property request) {
      final ParameterizedSparqlString pss = new ParameterizedSparqlString(
          ResourceCache.getInstance().get("/sparql/compatible-properties.rq"));
      pss.setIri("request", request.getType().getIdentifier());
      return pss.toString();
    }

    @Override
    protected PublishedProperty createOffer(final QuerySolution solution) {
      final Property offer = CachingResourceInternalizers.properties.internalize(solution.getResource("offer"));
      final Action action = CachingResourceInternalizers.actions.internalize(solution.getResource("action"));
      return new PublishedProperty(offer, action);
    }

  }

}
