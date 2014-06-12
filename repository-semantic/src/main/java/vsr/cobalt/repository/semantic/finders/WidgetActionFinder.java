/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.finders;

import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Widget;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.CachingResourceInternalizers;
import vsr.cobalt.repository.semantic.utils.CappedLinkedHashMap;
import vsr.cobalt.repository.semantic.utils.NodeToResourceMapper;

/**
 * @author Erik Wienhold
 */
public class WidgetActionFinder {

  public static final int DEFAULT_CACHE_SIZE = 32;

  private final Dataset dataset;

  private final CappedLinkedHashMap<Widget, Set<Action>> cache;

  public WidgetActionFinder(final Dataset dataset, final int cacheSize) {
    this.dataset = dataset;
    cache = new CappedLinkedHashMap<>(cacheSize);
  }

  public WidgetActionFinder(final Dataset dataset) {
    this(dataset, DEFAULT_CACHE_SIZE);
  }

  public Set<Action> findWidgetActions(final Widget widget) {
    Set<Action> actions = cache.get(widget);

    if (actions == null) {
      actions = getWidgetActions(widget);
      cache.put(widget, actions);
    }

    return actions;
  }

  private Set<Action> getWidgetActions(final Widget widget) {
    dataset.begin(ReadWrite.READ);
    try {
      final Model model = dataset.getDefaultModel();
      final Resource wr = model.createResource(widget.getIdentifier());
      final Iterator<Resource> ars = getActionResources(wr);
      return CachingResourceInternalizers.actions.internalizeAll(ars);
    } finally {
      dataset.end();
    }
  }

  private Iterator<Resource> getActionResources(final Resource widget) {
    return new NodeToResourceMapper(widget.getModel().listObjectsOfProperty(widget, Ontology.hasAction));
  }

}
