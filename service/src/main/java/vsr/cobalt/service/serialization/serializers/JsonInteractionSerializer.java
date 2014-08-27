/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serialization.serializers;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.models.Interaction;
import vsr.cobalt.service.serialization.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonInteractionSerializer extends JsonSerializer<Interaction> {

  private static final String instructionText = "instructionText";

  @Override
  public JsonObjectBuilder build(final Interaction interaction) {
    return Json.createObjectBuilder()
        .add(instructionText, interaction.getInstructionText());
  }

}
