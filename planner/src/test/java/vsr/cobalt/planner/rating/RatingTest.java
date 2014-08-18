/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.rating;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

@Test
public class RatingTest {

  @Test
  public void compareByValue() {
    final Rating r1 = new Rating(1);
    final Rating r2 = new Rating(3);
    assertEquals(r1.compareTo(r2), Double.compare(r1.getValue(), r2.getValue()));
  }

  @Test
  public void calculateHashCodeFromValue() {
    final Rating r = new Rating(13);
    assertEquals(r.hashCode(), Double.valueOf(13).hashCode());
  }

  @Test
  public void equalsWhenValuesEqual() {
    final Rating r1 = new Rating(1);
    final Rating r2 = new Rating(1);
    assertEquals(r1, r2);
  }

  @Test
  public void notEqualsWhenValuesDiffer() {
    final Rating r1 = new Rating(1);
    final Rating r2 = new Rating(2);
    assertNotEquals(r1, r2);
  }

  @Test
  public void notEqualsToObjectOfDifferentClass() {
    final Rating r = new Rating(1);
    final Object x = new Object();
    assertNotEquals(r, x);
  }

}
