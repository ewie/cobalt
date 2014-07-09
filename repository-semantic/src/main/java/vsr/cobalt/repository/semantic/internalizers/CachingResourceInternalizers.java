/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PropositionSet;
import vsr.cobalt.models.Type;
import vsr.cobalt.models.Widget;
import vsr.cobalt.repository.semantic.internalizers.resources.ActionResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.FunctionalityResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.InteractionResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.MashupResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.PropertyResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.PropositionsResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.TypeResourceInternalizer;
import vsr.cobalt.repository.semantic.internalizers.resources.WidgetResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public final class CachingResourceInternalizers {

  public static final CachingResourceInternalizer<Action> actions;
  public static final CachingResourceInternalizer<Mashup> mashups;
  public static final CachingResourceInternalizer<Property> properties;
  public static final CachingResourceInternalizer<PropositionSet> propositions;
  public static final CachingResourceInternalizer<Functionality> functionalities;
  public static final CachingResourceInternalizer<Type> types;
  public static final CachingResourceInternalizer<Interaction> interactions;
  public static final CachingResourceInternalizer<Widget> widgets;

  static {
    interactions = caching(new InteractionResourceInternalizer());
    functionalities = caching(new FunctionalityResourceInternalizer());
    types = caching(new TypeResourceInternalizer());
    widgets = caching(new WidgetResourceInternalizer());
    mashups = caching(new MashupResourceInternalizer(functionalities));
    properties = caching(new PropertyResourceInternalizer(types));
    propositions = caching(new PropositionsResourceInternalizer(properties));
    actions = caching(new ActionResourceInternalizer(widgets, propositions, properties, functionalities, interactions));
  }

  private static <T> CachingResourceInternalizer<T> caching(final ResourceInternalizer<T> internalizer) {
    return new CachingResourceInternalizer<>(internalizer);
  }

}
