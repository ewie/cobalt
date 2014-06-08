/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.rating;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class PlanRaterVisitorTest {

  private final Integer ZERO = 0;

  private PlanRaterVisitor visitor;

  @BeforeMethod
  public void setUp() {
    visitor = new PlanRaterVisitor();
  }

  @Test
  public void rateGraphNeutral() {
    assertEquals(visitor.rateGraph(null), ZERO);
  }

  @Test
  public void rateLevelNeutral() {
    assertEquals(visitor.rateLevel(null), ZERO);
  }

  @Test
  public void rateActionNeutral() {
    assertEquals(visitor.rateAction(null), ZERO);
  }

  @Test
  public void rateTaskProvisionNeutral() {
    assertEquals(visitor.rateTaskProvision(null), ZERO);
  }

  @Test
  public void rateActionProvisionNeutral() {
    assertEquals(visitor.rateActionProvision(null), ZERO);
  }

  @Test
  public void ratePropertyProvisionNeutral() {
    assertEquals(visitor.ratePropertyProvision(null), ZERO);
  }

}
