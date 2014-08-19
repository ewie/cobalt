/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

@Test
public class IdentifiableTest {

  private static class DummyIdentifiable extends Identifiable {

    public DummyIdentifiable(final Identifier identifier) {
      super(identifier);
    }

    @Override
    public boolean canEqual(final Object other) {
      return true;
    }

  }

  @Test
  public static class Equality {

    @Test
    public void calculateHashCode() {
      final Identifier id = Identifier.create("baz");
      final DummyIdentifiable provision = new DummyIdentifiable(id);
      assertEquals(provision.hashCode(), id.hashCode());
    }

    @Test
    public void equalWhenIdentifiersEqual() {
      final Identifier id = Identifier.create("bar");
      final DummyIdentifiable i1 = new DummyIdentifiable(id);
      final DummyIdentifiable i2 = new DummyIdentifiable(id);
      assertEquals(i1, i2);
    }

    @Test
    public void notEqualWhenOtherIsNoIdentifiable() {
      final DummyIdentifiable i = new DummyIdentifiable(Identifier.create(""));
      final Object x = new Object();
      assertNotEquals(i, x);
    }

    @Test
    public void notEqualWhenOtherCanEqualReturnsFalse() {
      final DummyIdentifiable i1 = new DummyIdentifiable(Identifier.create(""));
      final DummyIdentifiable i2 = mock(DummyIdentifiable.class);

      when(i2.canEqual(any())).thenReturn(false);

      assertNotEquals(i1, i2);
    }

  }

}
