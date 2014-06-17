/*
* Copyright (c) 2014, Erik Wienhold
* All rights reserved.
*
* Licensed under the BSD 3-Clause License.
*/

package vsr.cobalt.service.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import vsr.cobalt.service.Service;

/**
 * @author Erik Wienhold
 */
public class Listener implements ServletContextListener {

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    Service.getInstance().initializeDataset();
  }

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
  }

}
