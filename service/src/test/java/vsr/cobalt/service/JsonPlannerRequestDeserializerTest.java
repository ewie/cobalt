/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonWriter;

import org.testng.annotations.Test;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.models.Task;
import vsr.cobalt.service.planner.JsonPlannerRequestDeserializer;
import vsr.cobalt.service.planner.PlannerRequest;

import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class JsonPlannerRequestDeserializerTest {

  private static final String MASHUP = "mashup";

  private static final String CONTENT_TYPE = "contentType";

  private static final String CONTENT = "content";

  private static final String MIN_DEPTH = "minDepth";

  private static final String MAX_DEPTH = "maxDepth";

  private static String asJson(final JsonObject obj) {
    final StringWriter sw = new StringWriter();
    final JsonWriter jw = Json.createWriter(sw);
    jw.write(obj);
    return sw.toString();
  }

  private static Reader resource(final String name) {
    final URL url = ClassLoader.getSystemResource(name);
    final URI uri;
    try {
      uri = url.toURI();
    } catch (final URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
    final File f = new File(uri);
    try {
      return new FileReader(f);
    } catch (final FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static JsonStructure load(final String name) {
    return Json.createReader(resource(name)).read();
  }

  private static JsonStructure parse(final String json) {
    return Json.createReader(new StringReader(json)).read();
  }

  @Test
  public static class Deserialize {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a JSON object")
    public void rejectJsonArray() {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      d.deserialize(Json.createArrayBuilder().build());
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a mashup description")
    public void rejectMissingMashupDescription() {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = parse("{}");
      d.deserialize(obj);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a mashup description")
    public void rejectNullAsMashup() {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = parse("{\"mashup\":null}");
      d.deserialize(obj);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting positive minimum depth")
    public void rejectNonPositiveMinDepth() throws Exception {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = load("request/non-positive-min-depth.json");
      d.deserialize(obj);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting minimum depth to be less than or equal to maximum depth")
    public void rejectMinDepthGreaterThanMaxDepth() throws Exception {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = load("request/min-depth-greater-than-max-depth.json");
      d.deserialize(obj);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "unsupported content-type")
    public void rejectMashupDataOfUnsupportedContentType() throws Exception {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = load("request/unsupported-content-type.json");
      d.deserialize(obj);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting non-empty mashup content")
    public void rejectEmptyMashupContent() throws Exception {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = load("request/empty-mashup-data.json");
      d.deserialize(obj);
    }

    @Test
    public void parseRequest() throws Exception {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = load("request/valid.json");
      final PlannerRequest r = d.deserialize(obj);
      final Set<Task> ts = setOf(make(aTask().withIdentifier(URI.create("urn:example:task:bar"))));
      assertEquals(r.getMashup(), new Mashup(ts));
      assertEquals(r.getMinDepth(), 2);
      assertEquals(r.getMaxDepth(), 3);
    }

    @Test
    public void parseTextualMashupData() throws Exception {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = load("request/textual-mashup-data.json");
      final PlannerRequest r = d.deserialize(obj);
      final Set<Task> ts = setOf(make(aTask().withIdentifier(URI.create("urn:example:task:foo"))));
      assertEquals(r.getMashup(), new Mashup(ts));
    }

    @Test
    public void defaultToOneAsMinDepth() throws Exception {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = load("request/without-depth.json");
      final PlannerRequest r = d.deserialize(obj);
      assertEquals(r.getMinDepth(), 1);
    }

    @Test
    public void defaultToMaxIntegerAsMaxDepth() throws Exception {
      final JsonPlannerRequestDeserializer d = new JsonPlannerRequestDeserializer();
      final JsonStructure obj = load("request/without-depth.json");
      final PlannerRequest r = d.deserialize(obj);
      assertEquals(r.getMaxDepth(), Integer.MAX_VALUE);
    }

  }

}
