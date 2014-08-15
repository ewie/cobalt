/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.rating.RatedPlan;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonPlanSerializer extends JsonSerializer<RatedPlan> {

  private static final String extensionLevels = "extensionLevels";
  private static final String initialLevel = "initialLevel";
  private static final String rating = "rating";

  private final JsonSerializer<InitialLevel> initialLevelSerializer;
  private final JsonSerializer<ExtensionLevel> extensionLevelSerializer;

  public JsonPlanSerializer(final JsonSerializer<InitialLevel> initialLevelSerializer,
                            final JsonSerializer<ExtensionLevel> extensionLevelSerializer) {
    this.initialLevelSerializer = initialLevelSerializer;
    this.extensionLevelSerializer = extensionLevelSerializer;
  }

  @Override
  public JsonObjectBuilder build(final RatedPlan ratedPlan) {
    final Graph g = ratedPlan.getPlan().getGraph();
    return Json.createObjectBuilder()
        .add(rating, ratedPlan.getRating().getValue())
        .add(initialLevel, initialLevelSerializer.serialize(g.getInitialLevel()))
        .add(extensionLevels, extensionLevelSerializer.serializeAll(g.getExtensionLevels()));
  }

}
