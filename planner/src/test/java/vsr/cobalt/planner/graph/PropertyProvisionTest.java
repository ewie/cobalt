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
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PropertyProvisionTest {

  @Test
  public void extendsProvision() {
    assertSubClass(PropertyProvision.class, Provision.class);
  }

  @Test
  public static class New {

    @Test
    public void useOfferedSubjectAsRequestWhenCreatedFromOffer() {
      final Property p = make(aMinimalProperty());
      final Action a = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withFilled(p)));
      final PublishedProperty pub = new PublishedProperty(p, a);
      final PropertyProvision pp = new PropertyProvision(pub);
      assertSame(pp.getRequest(), pub.getSubject());
    }

  }

  @Test
  public static class CanEqual {

    private PropertyProvision pp;

    @BeforeMethod
    public void setUp() {
      final Property p = make(aMinimalProperty());
      final Action a = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withFilled(p)));
      final PublishedProperty pub = new PublishedProperty(p, a);
      pp = new PropertyProvision(p, pub);
    }

    @Test
    public void returnTrueWhenCalledWithPropertyProvision() {
      assertTrue(pp.canEqual(pp));
    }

    @Test
    public void returnFalseWhenCalledWithNonPropertyProvision() {
      final Object x = new Object();
      assertFalse(pp.canEqual(x));
    }

  }

}
