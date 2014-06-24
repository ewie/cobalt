/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.json;

/**
 * @author Erik Wienhold
 * @see <a href="http://tools.ietf.org/html/rfc6901">RFC 6901</a>
 */
public abstract class JsonPointer {

  private static final String SEP = "/";

  public static JsonPointer root() {
    return Root.INSTANCE;
  }

  /**
   * @return true when root, false otherwise
   */
  public abstract boolean isRoot();

  /**
   * @return the parent pointer
   *
   * @throws UnsupportedOperationException when not applicable
   */
  public abstract JsonPointer getParent();

  /**
   * @return the string representation per RFC 6901
   */
  public abstract String toString();

  /**
   * Create a JSON pointer referencing the level under this pointer.
   *
   * @param token a token of the next level
   *
   * @return a new JSON pointer
   */
  public JsonPointer path(final String token) {
    return new Path(token, this);
  }

  /**
   * Escape a token.
   *
   * @param token a token
   *
   * @return the escaped token per RFC 6901
   */
  private static String escape(final String token) {
    final StringBuilder s = new StringBuilder();
    for (int i = 0; i < token.length(); i += 1) {
      final Character c = token.charAt(i);
      switch (c) {
      case '~':
        s.append("~0");
        break;
      case '/':
        s.append("~1");
        break;
      default:
        s.append(c);
      }
    }
    return s.toString();
  }

  private static class Root extends JsonPointer {

    public static final Root INSTANCE = new Root();

    private Root() {
    }

    @Override
    public boolean isRoot() {
      return true;
    }

    @Override
    public JsonPointer getParent() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "";
    }

  }

  private static class Path extends JsonPointer {

    private final String token;

    private final JsonPointer parent;

    public Path(final String token, final JsonPointer parent) {
      this.token = token;
      this.parent = parent;
    }

    @Override
    public boolean isRoot() {
      return false;
    }

    @Override
    public JsonPointer getParent() {
      return parent;
    }

    @Override
    public String toString() {
      return parent + SEP + escape(token);
    }

  }

}
