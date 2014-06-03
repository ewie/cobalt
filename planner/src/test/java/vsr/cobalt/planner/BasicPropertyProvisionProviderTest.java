/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.models.Property;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropertyProvisionMaker.aPropertyProvision;

@Test
public class BasicPropertyProvisionProviderTest {

  @Test
  public void returnAllPropertyProvisionsReturnedByRepository() {
    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));

    final PropertyProvision pp1 = make(aPropertyProvision()
        .withRequest(p1)
        .withOffer(p1)
        .withProvidingAction(aMinimalAction().withPub(p1)));

    final PropertyProvision pp2 = make(aPropertyProvision()
        .withRequest(p2)
        .withOffer(p2)
        .withProvidingAction(aMinimalAction().withPub(p2)));

    final Repository r = mock(Repository.class);
    when(r.findCompatibleProperties(p1)).thenReturn(setOf(pp1));
    when(r.findCompatibleProperties(p2)).thenReturn(setOf(pp2));

    final BasicPropertyProvisionProvider ppp = new BasicPropertyProvisionProvider(r);

    assertEquals(ppp.getProvisionsFor(setOf(p1, p2)), setOf(pp1, pp2));
  }

}
