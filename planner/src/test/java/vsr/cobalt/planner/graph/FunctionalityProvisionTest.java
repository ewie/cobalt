/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.RealizedFunctionality;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class FunctionalityProvisionTest {

  @Test
  public void extendsProvision() {
    assertSubClass(FunctionalityProvision.class, Provision.class);
  }

  @Test
  public static class New {

    @Test
    public void useOfferedSubjectAsRequestWhenCreatedFromOffer() {
      final Functionality f = make(aMinimalFunctionality());
      final Action a = make(aMinimalAction().withFunctionality(f));
      final RealizedFunctionality rf = new RealizedFunctionality(f, a);
      final FunctionalityProvision fp = new FunctionalityProvision(rf);
      assertSame(fp.getRequest(), rf.getSubject());
    }

  }

  @Test
  public static class CanEqual {

    private FunctionalityProvision fp;

    @BeforeMethod
    public void setUp() {
      final Functionality f = make(aMinimalFunctionality());
      final Action a = make(aMinimalAction().withFunctionality(f));
      final RealizedFunctionality rf = new RealizedFunctionality(f, a);
      fp = new FunctionalityProvision(f, rf);
    }

    @Test
    public void returnTrueWhenCalledWithFunctionalityProvision() {
      assertTrue(fp.canEqual(fp));
    }

    @Test
    public void returnFalseWhenCalledWithNonFunctionalityProvision() {
      final Object x = new Object();
      assertFalse(fp.canEqual(x));
    }

  }

}
