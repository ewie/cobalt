/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Erik Wienhold
 */
public class ResourceCache {

  private static ResourceCache INSTANCE;

  private final Map<Path, String> index = new HashMap<>();

  public static ResourceCache getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ResourceCache();
    }
    return INSTANCE;
  }

  public String get(final String path) {
    final Path p = getPath(path);
    if (!contains(p)) {
      index.put(p, read(p));
    }
    return index.get(p);
  }

  private boolean contains(final Path path) {
    return index.containsKey(path);
  }

  private String read(final Path path) {
    final byte[] bytes;
    try {
      bytes = Files.readAllBytes(path);
    } catch (final IOException ex) {
      throw new RuntimeException(ex);
    }
    return new String(bytes);
  }

  private Path getPath(final String path) {
    return Paths.get(getUri(path));
  }

  private URI getUri(final String path) {
    final URL url = getClass().getResource(path);
    final URI uri;
    try {
      uri = url.toURI();
    } catch (final URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return uri;
  }

}
