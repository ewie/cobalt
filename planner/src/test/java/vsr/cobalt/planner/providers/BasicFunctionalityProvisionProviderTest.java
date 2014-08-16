/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.RealizedFunctionality;
import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.graph.FunctionalityProvision;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class BasicFunctionalityProvisionProviderTest {

  @Test
  public void returnAllFunctionalityProvisionsReturnedByRepository() {
    final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
    final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

    final Action a1 = make(aMinimalAction().withFunctionality(f1));
    final Action a2 = make(aMinimalAction().withFunctionality(f2));

    final Repository r = mock(Repository.class);
    when(r.findCompatibleOffers(f1)).thenReturn(setOf(realizedFunctionality(f1, a1)));
    when(r.findCompatibleOffers(f2)).thenReturn(setOf(realizedFunctionality(f2, a2)));

    final BasicFunctionalityProvisionProvider tpp = new BasicFunctionalityProvisionProvider(r);

    final FunctionalityProvision fp1 = functionalityProvision(f1, a1);
    final FunctionalityProvision fp2 = functionalityProvision(f2, a2);

    assertEquals(tpp.getProvisionsFor(setOf(f1, f2)), setOf(fp1, fp2));
  }

  private static FunctionalityProvision functionalityProvision(final Functionality f, final Action a) {
    return make(aFunctionalityProvision()
        .withRequest(f)
        .withOffer(f)
        .withProvidingAction(a));
  }

  private static RealizedFunctionality realizedFunctionality(final Functionality f, final Action a) {
    return new RealizedFunctionality(f, a);
  }

}
