/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vsr.cobalt.repository.semantic.DatasetPopulator;
import vsr.cobalt.repository.semantic.InvalidModelException;

/**
 * Seeds a {@link Dataset} by walking the directory tree starting at a given path and loading every file found within
 * the tree.
 *
 * @author Erik Wienhold
 */
public class DatasetSeeder {

  private static final Logger logger = LoggerFactory.getLogger(DatasetSeeder.class);

  private final Dataset dataset;

  private final Path dir;

  public DatasetSeeder(final Dataset dataset, final Path dir) {
    this.dataset = dataset;
    this.dir = dir;
  }

  public void seedDataset() {
    populateDataset(getPaths());
  }

  public void populateDataset(final Iterable<Path> paths) {
    final DatasetPopulator populator = new DatasetPopulator(dataset);
    for (final Path path : paths) {
      loadModel(path, populator);
    }
  }

  private void loadModel(final Path path, final DatasetPopulator populator) {
    logger.info("load {}", path);

    final Model model;

    try {
      model = RDFDataMgr.loadModel(path.toString());
    } catch (final RiotException ex) {
      logger.info("cannot read model {} {}", path, ex.getMessage());
      return;
    }

    try {
      populator.addModel(model);
    } catch (final InvalidModelException ex) {
      logger.info("invalid model {} {}", path, ex.getMessage());
    }
  }

  private Iterable<Path> getPaths() {
    final FileCollector fc = new FileCollector();
    try {
      Files.walkFileTree(dir, fc);
    } catch (final IOException ignored) {
    }
    return fc.getFiles();
  }

  private static class FileCollector implements FileVisitor<Path> {

    private final Collection<Path> files = new ArrayList<>();

    public Iterable<Path> getFiles() {
      return files;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path path, final BasicFileAttributes attrs) throws IOException {
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException {
      if (attrs.isRegularFile()) {
        files.add(path);
      }
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path path, final IOException e) throws IOException {
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path path, final IOException e) throws IOException {
      return FileVisitResult.CONTINUE;
    }
  }

}
