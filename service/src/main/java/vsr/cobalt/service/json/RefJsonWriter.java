/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.json;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;

/**
 * A JSON writer that writes objects once and uses JSON pointers to reference those objects when used elsewhere.
 * Uses object identity to detect already written objects.
 *
 * @author Erik Wienhold
 */
public class RefJsonWriter implements JsonWriter {

  private final Writer writer;

  private final boolean referenceEmpty;

  private final Map<JsonValue, JsonPointer> index = new HashMap<>();

  private JsonPointer pointer = JsonPointer.root();

  /**
   * @param writer         the writer to be written to
   * @param referenceEmpty true when empty objects or arrays should be referenced
   */
  public RefJsonWriter(final Writer writer, final boolean referenceEmpty) {
    this.writer = writer;
    this.referenceEmpty = referenceEmpty;
  }

  /**
   * Create a JSON writer which does not reference empty objects or arrays.
   *
   * @param writer the writer to be written to
   */
  public RefJsonWriter(final Writer writer) {
    this(writer, false);
  }

  @Override
  public void write(final JsonStructure value) {
    if (value instanceof JsonArray) {
      writeArray((JsonArray) value);
    } else if (value instanceof JsonObject) {
      writeObject((JsonObject) value);
    }
  }

  @Override
  public void writeArray(final JsonArray array) {
    cache(array);
    writeBeginArray();
    boolean first = true;
    for (int i = 0; i < array.size(); i += 1) {
      if (!first) {
        writeComma();
      }
      push(Integer.toString(i));
      writeValue(array.get(i));
      pop();
      first = false;
    }
    writeEndArray();
  }

  @Override
  public void writeObject(final JsonObject object) {
    cache(object);
    _writeObject(object);
  }

  @Override
  public void close() {
    try {
      writer.close();
    } catch (final IOException ignored) {
    }
  }

  private void cache(final JsonObject object) {
    if (!object.isEmpty() || referenceEmpty) {
      index.put(object, pointer);
    }
  }

  private void cache(final JsonArray array) {
    if (!array.isEmpty() || referenceEmpty) {
      index.put(array, pointer);
    }
  }

  private void push(final String name) {
    pointer = pointer.path(name);
  }

  private void pop() {
    pointer = pointer.getParent();
  }

  private JsonString asJsonString(final String s) {
    return Json.createArrayBuilder().add(s).build().getJsonString(0);
  }

  private void writeValue(final JsonValue value) {
    final JsonPointer p = index.get(value);
    if (p != null) {
      writeRef(p);
      return;
    }

    switch (value.getValueType()) {
    case ARRAY:
      writeArray((JsonArray) value);
      break;
    case OBJECT:
      writeObject((JsonObject) value);
      break;
    default:
      write(value.toString());
      break;
    }
  }

  private void writeRef(final JsonPointer pointer) {
    _writeObject(Json.createObjectBuilder().add("$ref", pointer.toString()).build());
  }

  private void _writeObject(final JsonObject object) {
    writeBeginObject();
    boolean first = true;
    for (final Map.Entry<String, JsonValue> e : object.entrySet()) {
      if (!first) {
        writeComma();
      }
      push(e.getKey());
      writeName(e.getKey());
      writeColon();
      writeValue(e.getValue());
      pop();
      first = false;
    }
    writeEndObject();
  }

  private void writeBeginArray() {
    write("[");
  }

  private void writeEndArray() {
    write("]");
  }

  private void writeBeginObject() {
    write("{");
  }

  private void writeEndObject() {
    write("}");
  }

  private void writeComma() {
    write(",");
  }

  private void writeColon() {
    write(":");
  }

  private void writeName(final String name) {
    writeValue(asJsonString(name));
  }

  private void write(final String s) {
    try {
      writer.write(s);
    } catch (final IOException ex) {
      throw new JsonException("cannot write JSON", ex);
    }
  }

}
