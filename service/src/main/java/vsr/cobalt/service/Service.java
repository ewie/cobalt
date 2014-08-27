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
import vsr.cobalt.planner.Repository;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.repository.semantic.SemanticRepository;
import vsr.cobalt.service.distance.FunctionalityProvisionDistanceMeter;
import vsr.cobalt.service.distance.PropertyProvisionDistanceMeter;
import vsr.cobalt.service.distance.ProvisionDistanceMeter;

/**
 * @author Erik Wienhold
 */
public class Service {

  private static final Logger logger = LoggerFactory.getLogger(Service.class);

  private static final Service INSTANCE = new Service();

  private Dataset dataset;

  private Repository repository;

  private Service() {
  }

  public static Service getInstance() {
    return INSTANCE;
  }

  public void initializeDataset() {
    if (createDataset()) {
      seedDataset();
    }
  }

  public Repository getRepository() {
    if (repository == null) {
      repository = new SemanticRepository(dataset);
    }
    return repository;
  }

  public ProvisionDistanceMeter<PropertyProvision> getPropertyDistanceMeter() {
    return new PropertyProvisionDistanceMeter(getRepository());
  }

  public ProvisionDistanceMeter<FunctionalityProvision> getFunctionalityDistanceMeter() {
    return new FunctionalityProvisionDistanceMeter(getRepository());
  }

  private boolean createDataset() {
    if (dataset == null) {
      final String dir = Config.get(Config.datasetDir);
      logger.info("create dataset {}", dir);
      dataset = TDBFactory.createDataset(dir);
      return true;
    }
    return false;
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
