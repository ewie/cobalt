/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.hp.hpl.jena.tdb.sys.Names;

/**
 * @author Erik Wienhold
 */
public final class Config {

  public static final Property<String> datasetDir = new Property<String>("datasetDir") {

    @Override
    public String parse(final String value) {
      return Paths.get(value).toString();
    }

    @Override
    public String getDefault() {
      return Names.memName;
    }

  };

  public static final Property<Boolean> seedDataset = new Property<Boolean>("seedDataset") {

    @Override
    public Boolean parse(final String value) {
      return Boolean.parseBoolean(value);
    }

    @Override
    public Boolean getDefault() {
      return false;
    }

  };

  public static final Property<Path> widgetDir = new Property<Path>("widgetDir") {

    @Override
    public Path parse(final String value) {
      return Paths.get(value);
    }

    @Override
    public Path getDefault() {
      return Paths.get(".");
    }

  };

  public static <T> T get(final Property<T> property) {
    final String value = System.getProperty(property.getName());
    if (value == null) {
      return property.getDefault();
    }
    return property.parse(value);
  }

  public static abstract class Property<T> {

    private final String namespace;

    private final String localName;

    public Property(final String namespace, final String localName) {
      this.namespace = namespace;
      this.localName = localName;
    }

    public Property(final String localName) {
      this(Config.class.getPackage().getName(), localName);
    }

    public String getName() {
      return namespace + "." + localName;
    }

    public abstract T parse(String value);

    public abstract T getDefault();

  }

}
