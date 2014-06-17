/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Widget;
import vsr.cobalt.planner.collectors.rating.RatedPlan;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.service.CachingJsonSerializer;

/**
 * @author Erik Wienhold
 */
public final class CachingJsonSerializers {

  public static final CachingJsonSerializer<Action> actions;
  public static final CachingJsonSerializer<ActionProvision> actionProvisons;
  public static final CachingJsonSerializer<ExtensionLevel> extensionLevels;
  public static final CachingJsonSerializer<InitialLevel> initialLevels;
  public static final CachingJsonSerializer<Interaction> interactions;
  public static final CachingJsonSerializer<RatedPlan> plans;
  public static final CachingJsonSerializer<Property> properties;
  public static final CachingJsonSerializer<PropertyProvision> propertyProvisions;
  public static final CachingJsonSerializer<Task> tasks;
  public static final CachingJsonSerializer<TaskProvision> taskProvisions;
  public static final CachingJsonSerializer<Widget> widgets;

  static {
    interactions = new CachingJsonSerializer<>(new JsonInteractionSerializer());
    properties = new CachingJsonSerializer<>(new JsonPropertySerializer());
    tasks = new CachingJsonSerializer<>(new JsonTaskSerializer());
    widgets = new CachingJsonSerializer<>(new JsonWidgetSerializer());

    actions = new CachingJsonSerializer<>(new JsonActionSerializer(widgets, tasks, properties, interactions));

    taskProvisions = new CachingJsonSerializer<>(new JsonTaskProvisionSerializer(actions, tasks));

    propertyProvisions = new CachingJsonSerializer<>(new JsonPropertyProvisionSerializer(actions, properties));

    actionProvisons = new CachingJsonSerializer<>(
        new JsonActionProvisionSerializer(actions, propertyProvisions));

    initialLevels = new CachingJsonSerializer<>(new JsonInitialLevelSerializer(taskProvisions));
    extensionLevels = new CachingJsonSerializer<>(new JsonExtensionLevelSerializer(actionProvisons));

    plans = new CachingJsonSerializer<>(new JsonPlanSerializer(initialLevels, extensionLevels));
  }
}
