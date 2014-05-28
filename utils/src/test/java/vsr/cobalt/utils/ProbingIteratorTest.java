/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.NoSuchElementException;

import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

@Test
public class ProbingIteratorTest {

  @Test
  public static class HasNext {

    @Test
    public void returnTrueWhenNextValueExists() {
      final ProbingIterator<?> bi = mock(ProbingIterator.class);
      when(bi.probeNextValue()).thenReturn(new Object());
      assertTrue(bi.hasNext());
    }

    @Test
    public void returnFalseWhenNoNextValueExists() {
      final ProbingIterator<?> bi = mock(ProbingIterator.class);
      when(bi.probeNextValue()).thenReturn(null);
      assertFalse(bi.hasNext());
    }

  }

  @Test
  public static class Next {

    @Test
    public void returnNextValueWhenExistent() {
      final ProbingIterator<?> bi = mock(ProbingIterator.class);
      final Object value = new Object();
      when(bi.probeNextValue()).thenReturn(value);
      assertSame(bi.next(), value);
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void throwWhenNoNextValueExists() {
      final ProbingIterator<?> bi = mock(ProbingIterator.class);
      when(bi.probeNextValue()).thenReturn(null);
      bi.next();
    }

  }

}
