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
  public JsonObjectBuilder build(final T identifiable) {
    final Identifier identifier = identifiable.getIdentifier();
    final JsonObjectBuilder obj = Json.createObjectBuilder();

    obj.add(id, identifier.toString());
    if (identifier.isUri()) {
      obj.add(uri, identifier.toString());
    }

    return obj;
  }

}
