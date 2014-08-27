/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serialization;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.service.planner.PlannerService;
import vsr.cobalt.service.serialization.serializers.JsonActionProvisionSerializer;
import vsr.cobalt.service.serialization.serializers.JsonActionSerializer;
import vsr.cobalt.service.serialization.serializers.JsonExtensionLevelSerializer;
import vsr.cobalt.service.serialization.serializers.JsonFunctionalityProvisionSerializer;
import vsr.cobalt.service.serialization.serializers.JsonFunctionalitySerializer;
import vsr.cobalt.service.serialization.serializers.JsonInitialLevelSerializer;
import vsr.cobalt.service.serialization.serializers.JsonInteractionSerializer;
import vsr.cobalt.service.serialization.serializers.JsonPlanSerializer;
import vsr.cobalt.service.serialization.serializers.JsonPropertyProvisionSerializer;
import vsr.cobalt.service.serialization.serializers.JsonPropertySerializer;
import vsr.cobalt.service.serialization.serializers.JsonWidgetSerializer;

/**
 * @author Erik Wienhold
 */
public final class CachingJsonSerializers {

  public static final CachingJsonSerializer<Action> actions;
  public static final CachingJsonSerializer<ActionProvision> actionProvisions;
  public static final CachingJsonSerializer<ExtensionLevel> extensionLevels;
  public static final CachingJsonSerializer<InitialLevel> initialLevels;
  public static final CachingJsonSerializer<Interaction> interactions;
  public static final CachingJsonSerializer<Plan> plans;
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
        functionalities, PlannerService.getInstance().getFunctionalityDistanceMeter()));

    propertyProvisions = new CachingJsonSerializer<>(new JsonPropertyProvisionSerializer(actions, properties,
        PlannerService.getInstance().getPropertyDistanceMeter()));

    actionProvisions = new CachingJsonSerializer<>(
        new JsonActionProvisionSerializer(actions, propertyProvisions));

    initialLevels = new CachingJsonSerializer<>(new JsonInitialLevelSerializer(functionalityProvisions));
    extensionLevels = new CachingJsonSerializer<>(new JsonExtensionLevelSerializer(actionProvisions));

    plans = new CachingJsonSerializer<>(new JsonPlanSerializer(initialLevels, extensionLevels));
  }
}
