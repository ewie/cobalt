/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serialization.serializers;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.service.serialization.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonPlanSerializer extends JsonSerializer<Plan> {

  private static final String extensionLevels = "extensionLevels";
  private static final String initialLevel = "initialLevel";

  private final JsonSerializer<InitialLevel> initialLevelSerializer;
  private final JsonSerializer<ExtensionLevel> extensionLevelSerializer;

  public JsonPlanSerializer(final JsonSerializer<InitialLevel> initialLevelSerializer,
                            final JsonSerializer<ExtensionLevel> extensionLevelSerializer) {
    this.initialLevelSerializer = initialLevelSerializer;
    this.extensionLevelSerializer = extensionLevelSerializer;
  }

  @Override
  public JsonObjectBuilder build(final Plan plan) {
    final Graph g = plan.getGraph();

    final JsonObjectBuilder obj = Json.createObjectBuilder()
        .add(initialLevel, initialLevelSerializer.serialize(g.getInitialLevel()));

    final JsonArray xls = extensionLevelSerializer.serializeAll(g.getExtensionLevels());
    if (!xls.isEmpty()) {
      obj.add(extensionLevels, xls);
    }

    return obj;
  }

}
