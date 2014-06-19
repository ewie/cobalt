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
import javax.json.Json;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vsr.cobalt.models.Task;
import vsr.cobalt.service.json.RefJsonWriter;
import vsr.cobalt.service.planner.JsonPlannerRequestDeserializer;
import vsr.cobalt.service.planner.JsonPlannerResponseSerializer;
import vsr.cobalt.service.planner.PlannerFailure;
import vsr.cobalt.service.planner.PlannerJob;
import vsr.cobalt.service.planner.PlannerRequest;
import vsr.cobalt.service.planner.PlannerResponse;
import vsr.cobalt.service.planner.PlannerService;

/**
 * @author Erik Wienhold
 */
public class PlannerServlet extends HttpServlet {

  private static final Logger logger = LoggerFactory.getLogger(PlannerServlet.class);

  private static final String CONTENT_TYPE_JSON = MediaType.JSON_UTF_8.toString();

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
    final PlannerRequest req;

    try {
      req = parseRequest(request.getReader());
    } catch (final Exception ex) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      response.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON);
      serializeResponse(new PlannerFailure(ex), response.getWriter());
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
    response.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON);
    serializeResponse(res, response.getWriter());
  }

  private PlannerRequest parseRequest(final Reader reader) {
    return new JsonPlannerRequestDeserializer().deserialize(Json.createReader(reader).read());
  }

  private void serializeResponse(final PlannerResponse response, final Writer writer) {
    new RefJsonWriter(writer).write(new JsonPlannerResponseSerializer().serialize(response));
  }

  private void logPlannerRequest(final PlannerRequest request) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);

    for (final Task t : request.getGoalMashup().getTasks()) {
      pw.printf("    task: %s", t.getIdentifier()).println();
    }

    logger.info("planner request:\n  minDepth: {}\n  maxDepth: {}\n  mashup:\n{}",
        request.getMinDepth(), request.getMaxDepth(), sw);
  }

}
