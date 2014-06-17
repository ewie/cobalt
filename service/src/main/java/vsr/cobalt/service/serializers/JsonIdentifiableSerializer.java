/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.models.Identifiable;
import vsr.cobalt.models.Identifier;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonIdentifiableSerializer<T extends Identifiable> extends JsonSerializer<T> {

  private static final String id = "id";
  private static final String uri = "uri";

  @Override
  public JsonObjectBuilder build(final T task) {
    final Identifier id = task.getIdentifier();
    return Json.createObjectBuilder().add(key(id), id.toString());
  }

  private String key(final Identifier identifier) {
    return identifier.isUri() ? uri : id;
  }

}
