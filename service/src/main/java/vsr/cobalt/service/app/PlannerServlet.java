/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.List;
import javax.json.Json;
import javax.json.JsonWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.planner.PlanningProblem;
import vsr.cobalt.service.planner.PlannerFailure;
import vsr.cobalt.service.planner.PlannerJob;
import vsr.cobalt.service.planner.PlannerRequest;
import vsr.cobalt.service.planner.PlannerResponse;
import vsr.cobalt.service.planner.PlannerService;
import vsr.cobalt.service.serialization.deserializers.JsonPlannerRequestDeserializer;
import vsr.cobalt.service.serialization.json.RefJsonWriter;
import vsr.cobalt.service.serialization.serializers.JsonPlannerResponseSerializer;

/**
 * @author Erik Wienhold
 */
public class PlannerServlet extends HttpServlet {

  private static final Logger logger = LoggerFactory.getLogger(PlannerServlet.class);

  private static final MediaType MEDIA_TYPE_JSON = MediaType.JSON_UTF_8.withoutParameters();

  private static final String JSON_POINTER = "json-pointer";

  private static final String JSON_POINTER_TRUE = "true";
  private static final String JSON_POINTER_FALSE = "false";

  @Override
  public void doOptions(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);

    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

    // allow every request header
    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
        request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS));

    // allowed methods
    response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.OPTIONS.asString());
    response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.POST.asString());
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final MediaType acceptedMediaType = parseAcceptHeader(request);

    if (acceptedMediaType == null) {
      response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
      response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      return;
    }

    final boolean acceptJsonPointer = acceptJsonPointer(acceptedMediaType);

    final PlannerRequest req;

    try {
      req = parseRequest(request.getReader());
    } catch (final Exception ex) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      response.setContentType(MediaType.JSON_UTF_8.toString());
      serializeResponseUsingCanonicalJson(new PlannerFailure(ex), response.getWriter());
      return;
    }

    logPlannerRequest(req);

    final PlannerJob job = PlannerService.getInstance().createJob(req);
    final PlannerResponse res = job.run();

    if (res.isSuccess()) {
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    response.setContentType(acceptedMediaType.toString());

    if (acceptJsonPointer) {
      serializeResponseUsingJsonPointer(res, response.getWriter());
    } else {
      serializeResponseUsingCanonicalJson(res, response.getWriter());
    }
  }

  private MediaType parseAcceptHeader(final HttpServletRequest request) {
    // check every accept header (in case the client sent multiple)
    final Enumeration<String> headers = request.getHeaders(HttpHeaders.ACCEPT);
    while (headers.hasMoreElements()) {
      final MediaType mediaType = MediaType.parse(headers.nextElement());
      if (isAcceptable(mediaType)) {
        return mediaType;
      }
    }
    return null;
  }

  private boolean isAcceptable(final MediaType mediaType) {
    // compare only type and subtype (ignore any parameters)
    return MEDIA_TYPE_JSON.equals(mediaType.withoutParameters());
  }

  private boolean acceptJsonPointer(final MediaType mediaType) {
    // media type must contain json-pointer=true
    final List<String> jps = mediaType.parameters().get(JSON_POINTER);
    return jps != null
        && !jps.isEmpty()
        && JSON_POINTER_TRUE.equals(jps.get(0));
  }

  private PlannerRequest parseRequest(final Reader reader) {
    return new JsonPlannerRequestDeserializer().deserialize(Json.createReader(reader).read());
  }

  private void serializeResponseUsingCanonicalJson(final PlannerResponse response, final Writer writer) {
    final JsonWriter w = Json.createWriter(writer);
    w.write(new JsonPlannerResponseSerializer().serialize(response));
  }

  private void serializeResponseUsingJsonPointer(final PlannerResponse response, final Writer writer) {
    new RefJsonWriter(writer).write(new JsonPlannerResponseSerializer().serialize(response));
  }

  private void logPlannerRequest(final PlannerRequest request) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);

    final PlanningProblem p = request.getPlanningProblem();

    for (final Functionality f : p.getGoalMashup().getFunctionalities()) {
      pw.printf("    functionality: %s", f.getIdentifier()).println();
    }

    logger.info("planner request:\n  minDepth: {}\n  maxDepth: {}\n  mashup:\n{}",
        p.getMinDepth(), p.getMaxDepth(), sw);
  }

}
