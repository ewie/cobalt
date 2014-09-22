/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author Erik Wienhold
 */
public final class Main {

  public static void main(final String[] args) throws Exception {
    final int port = Integer.parseInt(System.getProperty("jetty.port", "9000"));
    final Server server = new Server(port);

    final ProtectionDomain domain = Main.class.getProtectionDomain();
    final URL location = domain.getCodeSource().getLocation();

    final WebAppContext webapp = new WebAppContext();
    webapp.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
    webapp.setServer(server);
    webapp.setWar(location.toExternalForm());

    server.setHandler(webapp);
    server.start();
    server.join();
  }

}
