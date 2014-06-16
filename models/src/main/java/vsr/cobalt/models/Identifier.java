/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.net.URI;

/**
 * An identifier which can be either URI or string based.
 *
 * @author Erik Wienhold
 */
public abstract class Identifier {

  // Effectively seal the class by making its constructor private.
  private Identifier() {
  }

  public static Identifier create(final URI uri) {
    return new UriIdentifier(uri);
  }

  public static Identifier create(final String id) {
    return new StringIdentifier(id);
  }

  public abstract boolean isUri();

  public abstract URI getUri();

  public abstract String toString();

  protected abstract boolean equals(Identifier other);

  @Override
  public abstract int hashCode();

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Identifier
        && equals((Identifier) other);
  }

  private static class UriIdentifier extends Identifier {

    private final URI uri;

    public UriIdentifier(final URI uri) {
      this.uri = uri;
    }

    @Override
    public boolean isUri() {
      return true;
    }

    @Override
    public URI getUri() {
      return uri;
    }

    @Override
    public String toString() {
      return uri.toString();
    }

    @Override
    public int hashCode() {
      return uri.hashCode();
    }

    protected boolean equals(final Identifier other) {
      return other instanceof UriIdentifier
          && equals((UriIdentifier) other);
    }

    private boolean equals(final UriIdentifier other) {
      return uri.equals(other.uri);
    }

  }

  private static class StringIdentifier extends Identifier {

    private final String id;

    public StringIdentifier(final String id) {
      this.id = id;
    }

    @Override
    public boolean isUri() {
      return false;
    }

    @Override
    public URI getUri() {
      return null;
    }

    @Override
    public String toString() {
      return id;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }

    protected boolean equals(final Identifier other) {
      return other instanceof StringIdentifier
          && equals((StringIdentifier) other);
    }

    private boolean equals(final StringIdentifier other) {
      return id.equals(other.id);
    }

  }

}
