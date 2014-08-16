/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;
import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.graph.PropertyProvision;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class BasicPropertyProvisionProviderTest {

  @Test
  public void returnAllPropertyProvisionsReturnedByRepository() {
    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));

    final Action a1 = make(aMinimalAction()
        .withEffects(aPropositionSet()
            .withFilled(p1)));

    final Action a2 = make(aMinimalAction()
        .withEffects(aPropositionSet()
            .withFilled(p2)));

    final Repository r = mock(Repository.class);
    when(r.findCompatibleOffers(p1)).thenReturn(setOf(publishedProperty(p1, a1)));
    when(r.findCompatibleOffers(p2)).thenReturn(setOf(publishedProperty(p2, a2)));

    final BasicPropertyProvisionProvider ppp = new BasicPropertyProvisionProvider(r);


    final PropertyProvision pp1 = propertyProvision(p1, a1);
    final PropertyProvision pp2 = propertyProvision(p2, a2);

    assertEquals(ppp.getProvisionsFor(setOf(p1, p2)), setOf(pp1, pp2));
  }

  private static PropertyProvision propertyProvision(final Property p, final Action a) {
    return make(aPropertyProvision()
        .withRequest(p)
        .withOffer(p)
        .withProvidingAction(a));
  }

  private static PublishedProperty publishedProperty(final Property p, final Action a) {
    return new PublishedProperty(p, a);
  }

}
