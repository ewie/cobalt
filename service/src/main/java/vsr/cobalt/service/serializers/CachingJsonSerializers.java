/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.rating.RatedPlan;
import vsr.cobalt.service.CachingJsonSerializer;

/**
 * @author Erik Wienhold
 */
public final class CachingJsonSerializers {

  public static final CachingJsonSerializer<Action> actions;
  public static final CachingJsonSerializer<ActionProvision> actionProvisions;
  public static final CachingJsonSerializer<ExtensionLevel> extensionLevels;
  public static final CachingJsonSerializer<InitialLevel> initialLevels;
  public static final CachingJsonSerializer<Interaction> interactions;
  public static final CachingJsonSerializer<RatedPlan> plans;
  public static final CachingJsonSerializer<Property> properties;
  public static final CachingJsonSerializer<PropertyProvision> propertyProvisions;
  public static final CachingJsonSerializer<Functionality> functionalities;
  public static final CachingJsonSerializer<FunctionalityProvision> functionalityProvisions;
  public static final CachingJsonSerializer<Widget> widgets;

  static {
    interactions = new CachingJsonSerializer<>(new JsonInteractionSerializer());
    properties = new CachingJsonSerializer<>(new JsonPropertySerializer());
    functionalities = new CachingJsonSerializer<>(new JsonFunctionalitySerializer());
    widgets = new CachingJsonSerializer<>(new JsonWidgetSerializer());

    actions = new CachingJsonSerializer<>(new JsonActionSerializer(widgets, functionalities, properties, interactions));

    functionalityProvisions = new CachingJsonSerializer<>(new JsonFunctionalityProvisionSerializer(actions,
        functionalities));

    propertyProvisions = new CachingJsonSerializer<>(new JsonPropertyProvisionSerializer(actions, properties));

    actionProvisions = new CachingJsonSerializer<>(
        new JsonActionProvisionSerializer(actions, propertyProvisions));

    initialLevels = new CachingJsonSerializer<>(new JsonInitialLevelSerializer(functionalityProvisions));
    extensionLevels = new CachingJsonSerializer<>(new JsonExtensionLevelSerializer(actionProvisions));

    plans = new CachingJsonSerializer<>(new JsonPlanSerializer(initialLevels, extensionLevels));
  }
}
