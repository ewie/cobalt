/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropertyMaker.aProperty;
import static vsr.cobalt.testing.makers.PropertyProvisionMaker.aPropertyProvision;

@Test
public class ComposingPropertyProvisionProviderTest {

  @Test
  public void extendsComposingProvisionProvider() {
    assertSubClass(ComposingPropertyProvisionProvider.class, ComposingProvisionProvider.class);
  }

  @Test
  public static class FindProvisionsFor {

    @Test
    public void delegateToRepository() {
      final Property p = make(aMinimalProperty());
      final Set<PropertyProvision> tps = emptySet();

      final Repository r = mock(Repository.class);
      when(r.findCompatibleProperties(p)).thenReturn(tps);

      final ComposingPropertyProvisionProvider ppp = new ComposingPropertyProvisionProvider(r);
      assertSame(ppp.findProvisionsFor(p), tps);
    }

  }

  @Test
  public static class CreateProvision {

    @Test
    public void createPropertyProvision() {
      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));
      final Action a = make(aMinimalAction().withPub(p2));
      final PropertyProvision tp = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p2)
          .withProvidingAction(a));
      final ComposingPropertyProvisionProvider ppp = new ComposingPropertyProvisionProvider(null);
      assertEquals(ppp.createProvision(p1, p2, a), tp);
    }

  }

  @Test
  public static class GetOffers {

    @Test
    public void returnPublishedProperties() {
      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));
      final Action a = make(aMinimalAction().withPub(p1, p2));
      final ComposingPropertyProvisionProvider ppp = new ComposingPropertyProvisionProvider(null);
      assertEquals(ppp.getOffers(a), setOf(p1, p2));
    }

  }

}
