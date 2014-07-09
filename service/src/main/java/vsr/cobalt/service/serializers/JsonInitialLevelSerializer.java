/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonInitialLevelSerializer extends JsonSerializer<InitialLevel> {

  private static final String functionalityProvisions = "functionalityProvisions";

  private final JsonSerializer<FunctionalityProvision> functionalityProvisionSerializer;

  public JsonInitialLevelSerializer(final JsonSerializer<FunctionalityProvision> functionalityProvisionSerializer) {
    this.functionalityProvisionSerializer = functionalityProvisionSerializer;
  }

  @Override
  public JsonObjectBuilder build(final InitialLevel level) {
    return Json.createObjectBuilder()
        .add(functionalityProvisions, functionalityProvisionSerializer.serializeAll(level.getFunctionalityProvisions
            ()));
  }

}
