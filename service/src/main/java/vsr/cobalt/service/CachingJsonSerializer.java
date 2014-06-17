/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import java.util.WeakHashMap;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Erik Wienhold
 */
public class CachingJsonSerializer<T> extends JsonSerializer<T> {

  private final WeakHashMap<T, JsonObject> cache = new WeakHashMap<>();

  private final JsonSerializer<T> serializer;

  public CachingJsonSerializer(final JsonSerializer<T> serializer) {
    this.serializer = serializer;
  }

  @Override
  public JsonObject serialize(final T object) {
    JsonObject obj = cache.get(object);
    if (obj == null) {
      obj = serializer.serialize(object);
      cache.put(object, obj);
    }
    return obj;
  }

  @Override
  public JsonObjectBuilder build(final T object) {
    return serializer.build(object);
  }

}
