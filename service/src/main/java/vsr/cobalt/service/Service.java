/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import java.nio.file.Path;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Erik Wienhold
 */
public class Service {

  private static final Logger logger = LoggerFactory.getLogger(Service.class);

  private static final Service INSTANCE = new Service();

  private Dataset dataset;

  private Service() {
  }

  public static Service getInstance() {
    return INSTANCE;
  }

  public Dataset getDataset() {
    if (dataset == null) {
      createDataset();
    }
    return dataset;
  }

  public void createDataset() {
    if (dataset != null) {
      throw new IllegalStateException("dataset already initialized");
    }
    final String dir = Config.get(Config.datasetDir);
    logger.info("create dataset {}", dir);
    dataset = TDBFactory.createDataset(dir);
    seedDataset();
  }

  private void seedDataset() {
    if (Config.get(Config.seedDataset)) {
      final Path dir = Config.get(Config.widgetDir);
      logger.info("seed dataset {}", dir);
      final DatasetSeeder dsb = new DatasetSeeder(dataset, Config.get(Config.widgetDir));
      dsb.seedDataset();
    }
  }

}
