/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import java.util.Set;

import com.hp.hpl.jena.query.Dataset;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;
import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Type;
import vsr.cobalt.models.Widget;
import vsr.cobalt.repository.semantic.finders.CompatibleResourceFinder;
import vsr.cobalt.repository.semantic.finders.RequestOfferDistanceFinder;
import vsr.cobalt.repository.semantic.finders.WidgetActionFinder;

/**
 * A repository using a semantic model to provide planning information.
 *
 * @author Erik Wienhold
 */
public class SemanticRepository implements Repository {

  public static final int DEFAULT_CACHE_SIZE = 32;

  private final WidgetActionFinder widgetActionFinder;

  private final RequestOfferDistanceFinder requestOfferDistanceFinder;

  private final CompatibleResourceFinder compatibleResourceFinder;

  /**
   * Create a semantic repository using a dataset and cache size.
   *
   * @param dataset   a transactional dataset
   * @param cacheSize the cache size
   */
  public SemanticRepository(final Dataset dataset, final int cacheSize) {
    widgetActionFinder = new WidgetActionFinder(dataset, cacheSize);
    requestOfferDistanceFinder = new RequestOfferDistanceFinder(dataset, cacheSize);
    compatibleResourceFinder = new CompatibleResourceFinder(dataset, cacheSize);
  }

  /**
   * Create a semantic repository using the default cache size {@link #DEFAULT_CACHE_SIZE}.
   *
   * @param dataset a transactional dataset
   */
  public SemanticRepository(final Dataset dataset) {
    this(dataset, DEFAULT_CACHE_SIZE);
  }

  @Override
  public Set<Action> getWidgetActions(final Widget widget) {
    return widgetActionFinder.findWidgetActions(widget);
  }

  @Override
  public Set<RealizedTask> findCompatibleTasks(final Task task) {
    return compatibleResourceFinder.findCompatibleTasks(task);
  }

  @Override
  public Set<PublishedProperty> findCompatibleProperties(final Property property) {
    return compatibleResourceFinder.findCompatibleProperties(property);
  }

  @Override
  public int getDistance(final Task request, final Task offer) {
    return requestOfferDistanceFinder.getDistance(request, offer);
  }

  @Override
  public int getDistance(final Type request, final Type offer) {
    return requestOfferDistanceFinder.getDistance(request, offer);
  }

}
