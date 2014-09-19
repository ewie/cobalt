/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.repository.semantic.utils.ResourceCache;

/**
 * Utility to populate a dataset with models.
 *
 * @author Erik Wienhold
 */
public class DatasetPopulator {

  private final Dataset dataset;

  /**
   * @param dataset a transactional dataset
   */
  public DatasetPopulator(final Dataset dataset) {
    this.dataset = dataset;
  }

  /**
   * Validate a model, infer statements and add it to the dataset
   *
   * @param model the model to add
   *
   * @throws InvalidModelException when the model is invalid according to the ontology
   */
  public void addModel(final Model model) throws InvalidModelException {
    // Expect each model's graph to be disjoint from all other model graphs. When that's not the case,
    // the union of all graphs may result in an invalid model, because one model could contain statements
    // incompatible with statements of another model.
    assertModel(model);

    inferPropertyNames(model);

    dataset.begin(ReadWrite.WRITE);
    try {
      dataset.getDefaultModel().add(model);
      dataset.commit();
    } finally {
      dataset.end();
    }
  }

  private static void assertModel(final Model model) throws InvalidModelException {
    if (containsMutexPropositions(model)) {
      throw new InvalidModelException("model contains mutual exclusive propositions");
    }
  }

  private static boolean containsMutexPropositions(final Model model) {

    try (QueryExecution qx = createQuery(model, getQuery("/sparql/mutex-propositions.rq"))) {
      final ResultSet rs = qx.execSelect();
      if (rs.hasNext()) {
        return true;
      }
    }

    return false;
  }

  private static void inferPropertyNames(final Model model) {

    final Model names = ModelFactory.createDefaultModel();

    try (QueryExecution qx = createQuery(model, getQuery("/sparql/nameless-properties.rq"))) {
      final ResultSet rs = qx.execSelect();
      while (rs.hasNext()) {
        final QuerySolution qs = rs.next();
        final Resource p = qs.getResource("property");
        final Resource t = qs.getResource("type");
        names.add(p, Ontology.hasName, t.getURI());
      }
    }

    model.add(names);
  }

  private static String getQuery(final String path) {
    return ResourceCache.getInstance().get(path);
  }

  private static QueryExecution createQuery(final Model model, final String query) {
    return QueryExecutionFactory.create(query, model);
  }

}
