/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers;

import vsr.cobalt.repository.semantic.internalizers.resources.CachingActionResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.CachingInteractionResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.CachingMashupResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.CachingPropertyResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.CachingPropositionsResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.CachingTaskResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.CachingTypeResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.CachingWidgetResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public final class CachingResourceInternalizers {

  public static final CachingActionResourceInternalizer actions;
  public static final CachingMashupResourceInternalizer mashups;
  public static final CachingPropertyResourceInternalizer properties;
  public static final CachingPropositionsResourceInternalizer propositions;
  public static final CachingTaskResourceInternalizer tasks;
  public static final CachingTypeResourceInternalizer types;
  public static final CachingInteractionResourceInternalizer interactions;
  public static final CachingWidgetResourceInternalizer widgets;

  static {
    interactions = new CachingInteractionResourceInternalizer();
    tasks = new CachingTaskResourceInternalizer();
    types = new CachingTypeResourceInternalizer();
    widgets = new CachingWidgetResourceInternalizer();
    mashups = new CachingMashupResourceInternalizer(tasks);
    properties = new CachingPropertyResourceInternalizer(types);
    propositions = new CachingPropositionsResourceInternalizer(properties);
    actions = new CachingActionResourceInternalizer(widgets, propositions, properties, tasks, interactions);
  }

}
