/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.net.URI;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test
public class IdentifierTest {

  @Test
  public void createWithUri() {
    final URI uri = URI.create("urn:example:foo");
    final Identifier id = Identifier.create(uri);
    assertTrue(id.isUri());
    assertEquals(id.getUri(), uri);
  }

  @Test
  public void createWithString() {
    final String s = "foo";
    final Identifier id = Identifier.create(s);
    assertFalse(id.isUri());
    assertNull(id.getUri());
  }

  @Test
  public static class WithUri {

    @Test
    public void hashCodeFromUri() {
      final URI uri = URI.create("urn:example:bar");
      final Identifier id = Identifier.create(uri);
      assertEquals(id.hashCode(), uri.hashCode());
    }

    @Test
    public void equalWhenUrisEqual() {
      final Identifier id1 = Identifier.create(URI.create("urn:example:x"));
      final Identifier id2 = Identifier.create(URI.create("urn:example:x"));
      assertEquals(id1, id2);
    }

    @Test
    public void notEqualWhenUrisDiffer() {
      final Identifier id1 = Identifier.create(URI.create("urn:example:x"));
      final Identifier id2 = Identifier.create(URI.create("urn:example:y"));
      assertNotEquals(id1, id2);
    }

    @Test
    public void notEqualToObjectOfDifferentClass() {
      final Identifier id = Identifier.create(URI.create("urn:example:x"));
      final Object x = new Object();
      assertNotEquals(id, x);
    }

    @Test
    public void notEqualToStringIdentifier() {
      final Identifier id1 = Identifier.create(URI.create("urn:example:x"));
      final Identifier id2 = Identifier.create("urn:example:x");
      assertNotEquals(id1, id2);
    }

  }

  @Test
  public static class WithString {

    @Test
    public void hashCodeFromString() {
      final String s = "bar";
      final Identifier id = Identifier.create(s);
      assertEquals(id.hashCode(), s.hashCode());
    }

    @Test
    public void equalWhenStringsEqual() {
      final Identifier id1 = Identifier.create("x");
      final Identifier id2 = Identifier.create("x");
      assertEquals(id1, id2);
    }

    @Test
    public void notEqualWhenStringsDiffer() {
      final Identifier id1 = Identifier.create("x");
      final Identifier id2 = Identifier.create("y");
      assertNotEquals(id1, id2);
    }

    @Test
    public void notEqualToObjectOfDifferentClass() {
      final Identifier id = Identifier.create("x");
      final Object x = new Object();
      assertNotEquals(id, x);
    }

    @Test
    public void notEqualToStringIdentifier() {
      final Identifier id1 = Identifier.create("urn:example:x");
      final Identifier id2 = Identifier.create(URI.create("urn:example:x"));
      assertNotEquals(id1, id2);
    }

  }

}
