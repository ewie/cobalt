/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Erik Wienhold
 */
public class ResourceCache {

  private static ResourceCache INSTANCE;

  private final Map<String, String> index = new HashMap<>();

  public static ResourceCache getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ResourceCache();
    }
    return INSTANCE;
  }

  public String get(final String path) {
    String content = index.get(path);
    if (content == null) {
      content = read(path);
      if (content != null) {
        index.put(path, content);
      }
    }
    return content;
  }

  private String read(final String path) {
    final InputStream in = getResource(path);
    if (in == null) {
      return null;
    }
    return inputStreamToString(in);
  }

  private InputStream getResource(final String path) {
    return getClass().getResourceAsStream(path);
  }

  private String inputStreamToString(final InputStream in) {
    final BufferedReader br = new BufferedReader(new InputStreamReader(in));
    final StringBuilder sb = new StringBuilder();
    while (true) {
      final String line;
      try {
        line = br.readLine();
      } catch (final IOException ex) {
        break;
      }
      if (line == null) {
        break;
      }
      sb.append(line).append("\n");
    }
    return sb.toString();
  }

}
