/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serialization.serializers;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.service.serialization.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonExtensionLevelSerializer extends JsonSerializer<ExtensionLevel> {

  private static final String actionProvisions = "actionProvisions";

  private final JsonSerializer<ActionProvision> actionProvisionSerializer;

  public JsonExtensionLevelSerializer(final JsonSerializer<ActionProvision> actionProvisionSerializer) {
    this.actionProvisionSerializer = actionProvisionSerializer;
  }

  @Override
  public JsonObjectBuilder build(final ExtensionLevel level) {
    return Json.createObjectBuilder()
        .add(actionProvisions, actionProvisionSerializer.serializeAll(level.getActionProvisions()));
  }

}
