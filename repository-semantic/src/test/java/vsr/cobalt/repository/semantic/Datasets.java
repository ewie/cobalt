/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.apache.jena.riot.RDFDataMgr;

/**
 * @author Erik Wienhold
 */
public final class Datasets {

  /**
   * Create a transactional in-memory dataset and load data from a given URI.
   * <p/>
   * The dataset returned by {@link RDFDataMgr#loadDataset(String)} is not transactional.
   *
   * @param uri URI of data to load
   *
   * @return a new dataset
   */
  public static Dataset loadDataset(final String uri) {
    final Dataset ds = TDBFactory.createDataset();
    ds.begin(ReadWrite.WRITE);
    try {
      RDFDataMgr.read(ds, uri);
      ds.commit();
    } finally {
      ds.end();
    }
    return ds;
  }

}
