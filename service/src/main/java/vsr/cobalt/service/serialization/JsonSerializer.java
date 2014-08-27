/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serialization;

import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Erik Wienhold
 */
public abstract class JsonSerializer<T> {

  public abstract JsonObjectBuilder build(T object);

  public JsonObject serialize(final T object) {
    return build(object).build();
  }

  public JsonArray serializeAll(final Iterable<T> objects) {
    return serializeAll(objects.iterator());
  }

  public JsonArray serializeAll(final Iterator<T> objects) {
    final JsonArrayBuilder ary = Json.createArrayBuilder();
    while (objects.hasNext()) {
      ary.add(serialize(objects.next()));
    }
    return ary.build();
  }

}
