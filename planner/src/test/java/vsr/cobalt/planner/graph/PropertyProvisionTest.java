/*
* Copyright (c) 2014, Erik Wienhold
* All rights reserved.
*
* Licensed under the BSD 3-Clause License.
*/

package vsr.cobalt.planner.graph;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;
import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Task;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PropertyProvisionTest {

  @Test
  public void extendsIdentifiable() {
    assertSubClass(PropertyProvision.class, Provision.class);
  }

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting the offered property to be published by providing action")
    public void rejectOfferedTaskWhenNotRealizedByGivenAction() {
      final Action action = make(aMinimalAction());
      final Property property = make(aMinimalProperty());
      new PropertyProvision(null, property, action);
    }

    @Test
    public void createFromPublishedProperty() {
      final Property r = make(aMinimalProperty().withName("p1"));
      final Property p = make(aMinimalProperty().withName("p2"));
      final Action a = make(aMinimalAction().withPub(p));
      final PublishedProperty pub = new PublishedProperty(p, a);
      final PropertyProvision pp = new PropertyProvision(r, pub);
      assertSame(pp.getRequest(), r);
      assertSame(pp.getOffer(), pub.getProperty());
      assertSame(pp.getProvidingAction(), pub.getAction());
    }

  }

  @Test
  public static class CanEqual {

    @Test
    public void returnTrueWhenCalledWithPropertyProvision() {
      final Property p = make(aMinimalProperty());
      final Action a = make(aMinimalAction().withPub(p));
      final PropertyProvision pp1 = new PropertyProvision(null, p, a);
      final PropertyProvision pp2 = new PropertyProvision(null, p, a);
      assertTrue(pp1.canEqual(pp2));
    }

    @Test
    public void returnFalseWhenCalledWithNonPropertyProvision() {
      final Property p = make(aMinimalProperty());
      final Action a = make(aMinimalAction().withPub(p));
      final PropertyProvision pp = new PropertyProvision(null, p, a);
      final Object x = new Object();
      assertFalse(pp.canEqual(x));
    }

  }

}
