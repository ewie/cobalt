/*
* Copyright (c) 2014, Erik Wienhold
* All rights reserved.
*
* Licensed under the BSD 3-Clause License.
*/

package vsr.cobalt.service.planner;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.stream.JsonParsingException;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.repository.semantic.internalizers.models.MashupInternalizer;

/**
 * @author Erik Wienhold
 */
public class JsonPlannerRequestDeserializer {

  private static final String MASHUP = "mashup";
  private static final String CONTENT_TYPE = "contentType";
  private static final String CONTENT = "content";
  private static final String MIN_DEPTH = "minDepth";
  private static final String MAX_DEPTH = "maxDepth";

  public PlannerRequest deserialize(final JsonStructure objOrAry) {
    if (objOrAry instanceof JsonArray) {
      throw new IllegalArgumentException("expecting a JSON object");
    }

    final JsonObject obj = (JsonObject) objOrAry;

    final int minDepth = getMinDepth(obj);
    final int maxDepth = getMaxDepth(obj);
    final Mashup mashup = getMashup(obj);

    return new PlannerRequest(mashup, minDepth, maxDepth);
  }

  private Mashup getMashup(final JsonObject obj) {
    final JsonValue val = obj.get(MASHUP);
    if (val == null || val.getValueType() == JsonValue.ValueType.NULL) {
      throw new IllegalArgumentException("expecting a mashup description");
    }
    final JsonObject mashupObj = (JsonObject) val;
    final Lang lang;
    final String data;
    if (mashupObj.containsKey(CONTENT_TYPE)) {
      lang = RDFLanguages.nameToLang(mashupObj.getString(CONTENT_TYPE));
      if (lang == null) {
        throw new IllegalArgumentException("unsupported content-type");
      }
      data = mashupObj.getString(CONTENT);
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
    return obj.getInt(MIN_DEPTH, PlannerRequest.MIN_DEPTH);
  }

  private int getMaxDepth(final JsonObject obj) {
    return obj.getInt(MAX_DEPTH, PlannerRequest.MAX_DEPTH);
  }

  private String stringify(final JsonStructure struct) {
    final StringWriter sw = new StringWriter();
    final JsonWriter jw = Json.createWriter(sw);
    jw.write(struct);
    return sw.toString();
  }

  private JsonStructure read(final Reader reader) {
    try {
      return Json.createReader(reader).read();
    } catch (final JsonParsingException ex) {
      throw new IllegalArgumentException("expecting valid JSON", ex);
    }
  }

}
