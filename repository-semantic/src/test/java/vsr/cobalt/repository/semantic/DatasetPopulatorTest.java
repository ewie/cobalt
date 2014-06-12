/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.testng.annotations.Test;

import static org.apache.jena.riot.RDFDataMgr.loadModel;
import static org.testng.Assert.assertTrue;

@Test
public class DatasetPopulatorTest {

  @Test(expectedExceptions = InvalidModelException.class,
      expectedExceptionsMessageRegExp = "model contains mutual exclusive propositions")
  public void rejectMutexPropositions() throws Exception {
    final Dataset ds = TDBFactory.createDataset();
    final DatasetPopulator dsp = new DatasetPopulator(ds);
    dsp.addModel(loadModel("validation/mutex-propositions.n3"));
  }

  @Test
  public void addModel() throws Exception {
    final Dataset ds = TDBFactory.createDataset();
    final DatasetPopulator dsp = new DatasetPopulator(ds);

    final Model model = ModelFactory.createDefaultModel();
    final Resource s = model.createResource();
    final Property p = model.createProperty("urn:example:prop", "foo");
    final Resource o = model.createResource();
    model.add(s, p, o);

    dsp.addModel(model);

    ds.begin(ReadWrite.READ);

    try {
      assertTrue(ds.getDefaultModel().containsAll(model));
    } finally {
      ds.end();
    }
  }

  @Test
  public void inferMissingPropertyNames() throws Exception {
    final Dataset ds = TDBFactory.createDataset();
    final DatasetPopulator dsp = new DatasetPopulator(ds);
    dsp.addModel(loadModel("infer-property-names/data.n3"));

    final Model x = loadModel("infer-property-names/expected.n3");

    ds.begin(ReadWrite.READ);

    try {
      final Model m = ds.getDefaultModel();
      assertTrue(m.containsAll(x));
    } finally {
      ds.end();
    }
  }

}
