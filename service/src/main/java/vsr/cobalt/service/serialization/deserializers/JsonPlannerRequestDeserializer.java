/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serialization.deserializers;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.repository.semantic.internalizers.models.MashupInternalizer;
import vsr.cobalt.service.planner.ActionCompositionStrategy;
import vsr.cobalt.service.planner.PlannerRequest;
import vsr.cobalt.service.planner.PrecursorCompositionStrategy;

/**
 * @author Erik Wienhold
 */
public class JsonPlannerRequestDeserializer {

  private static final String mashup = "mashup";
  private static final String mediaType = "mediaType";
  private static final String content = "content";
  private static final String minDepth = "minDepth";
  private static final String maxDepth = "maxDepth";
  private static final String actionComposition = "actionComposition";
  private static final String precursorActions = "precursorActions";
  private static final String functionalityProviders = "functionalityProviders";
  private static final String propertyProviders = "propertyProviders";

  public PlannerRequest deserialize(final JsonStructure objOrAry) {
    if (objOrAry instanceof JsonArray) {
      throw new IllegalArgumentException("expecting a JSON object");
    }

    final JsonObject obj = (JsonObject) objOrAry;

    final int minDepth = getMinDepth(obj);
    final int maxDepth = getMaxDepth(obj);
    final Mashup mashup = getMashup(obj);
    final ActionCompositionStrategy compositionStrategy = getCompositionStrategy(obj);

    return new PlannerRequest(mashup, minDepth, maxDepth, compositionStrategy);
  }

  private Mashup getMashup(final JsonObject obj) {
    final JsonValue val = obj.get(mashup);
    if (val == null || !(val instanceof JsonObject)) {
      throw new IllegalArgumentException("expecting a mashup description");
    }
    final JsonObject mashupObj = (JsonObject) val;
    final Lang lang;
    final String data;
    if (mashupObj.containsKey(mediaType)) {
      // XXX accept any supported language, although the request schema only allows a subset (application/rdf+json,
      // application/rdf+xml, text/turtle).
      lang = RDFLanguages.nameToLang(mashupObj.getString(mediaType));
      if (lang == null) {
        throw new IllegalArgumentException("unsupported media type");
      }
      data = mashupObj.getString(content);
      if (Strings.isNullOrEmpty(data)) {
        throw new IllegalArgumentException("expecting non-empty mashup content");
      }
    } else {
      data = stringify(mashupObj);
      lang = Lang.RDFJSON;
    }
    return parseMashup(data, lang);
  }

  private Mashup parseMashup(final String s, final Lang lang) {
    final Model model = ModelFactory.createDefaultModel();
    model.read(new StringReader(s), null, lang.getLabel());
    final MashupInternalizer mi = new MashupInternalizer();
    final Set<Mashup> mashups = mi.internalize(model);
    if (mashups.isEmpty()) {
      return null;
    }
    return Iterables.get(mashups, 0);
  }

  private int getMinDepth(final JsonObject obj) {
    final Integer val = getInt(obj, minDepth, PlannerRequest.MIN_DEPTH);
    if (val == null) {
      throw new IllegalArgumentException("expecting minimum depth to be an integer");
    }
    return val;
  }

  private int getMaxDepth(final JsonObject obj) {
    final Integer val = getInt(obj, maxDepth, PlannerRequest.MAX_DEPTH);
    if (val == null) {
      throw new IllegalArgumentException("expecting maximum depth to be an integer");
    }
    return val;
  }

  private ActionCompositionStrategy getCompositionStrategy(final JsonObject obj) {
    final JsonValue val = obj.get(actionComposition);
    if (val == null || val == JsonValue.NULL) {
      return ActionCompositionStrategy.getDefault();
    }
    if (!(val instanceof JsonObject)) {
      throw new IllegalArgumentException("expecting an object specifying the action composition strategy");
    }
    final JsonObject obj2 = (JsonObject) val;
    final PrecursorCompositionStrategy a = parsePrecursorCompositionSrategy(getString(obj2, precursorActions, "none"));
    final boolean b = getBoolean(obj2, functionalityProviders, false);
    final boolean c = getBoolean(obj2, propertyProviders, false);
    return new ActionCompositionStrategy(a, b, c);
  }

  private PrecursorCompositionStrategy parsePrecursorCompositionSrategy(final String s) {
    try {
      return PrecursorCompositionStrategy.valueOf(s);
    } catch (final Exception ex) {
      throw new IllegalArgumentException("unsupported precursor composition strategy", ex);
    }
  }

  private Integer getInt(final JsonObject obj, final String key, final int defaultValue) {
    final JsonValue val = obj.get(key);
    if (val == null || val == JsonValue.NULL) {
      return defaultValue;
    }
    if (val instanceof JsonNumber) {
      return ((JsonNumber) val).intValue();
    }
    return null;
  }

  private String getString(final JsonObject obj, final String key, final String defaultValue) {
    final JsonValue val = obj.get(key);
    if (val == null || val == JsonValue.NULL) {
      return defaultValue;
    }
    if (val instanceof JsonString) {
      return ((JsonString) val).getString();
    }
    return null;
  }

  private Boolean getBoolean(final JsonObject obj, final String key, final boolean defaultValue) {
    final JsonValue val = obj.get(key);
    if (val == null || val == JsonValue.NULL) {
      return defaultValue;
    }
    if (val == JsonValue.FALSE) {
      return false;
    }
    if (val == JsonValue.TRUE) {
      return true;
    }
    return null;
  }

  private String stringify(final JsonStructure struct) {
    final StringWriter sw = new StringWriter();
    final JsonWriter jw = Json.createWriter(sw);
    jw.write(struct);
    return sw.toString();
  }

}
