/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.models.Property;
import vsr.cobalt.models.Type;
import vsr.cobalt.service.CachingJsonSerializer;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonPropertySerializer extends JsonSerializer<Property> {

  private static final String name = "name";
  private static final String type = "type";

  private final JsonSerializer<Type> types = new CachingJsonSerializer<>(new JsonTypeSerializer());

  @Override
  public JsonObjectBuilder build(final Property property) {
    return Json.createObjectBuilder()
        .add(name, property.getName())
        .add(type, types.serialize(property.getType()));
  }

}
