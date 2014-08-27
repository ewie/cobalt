/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders.providers;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.RealizedFunctionality;
import vsr.cobalt.planner.Repository;
import vsr.cobalt.planner.graph.FunctionalityProvision;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ComposingFunctionalityProvisionProviderTest {

  @Test
  public void extendsComposingProvisionProvider() {
    assertSubClass(ComposingFunctionalityProvisionProvider.class, ComposingProvisionProvider.class);
  }

  private static RealizedFunctionality realizedFunctionality(final Functionality f, final Action a) {
    return new RealizedFunctionality(f, a);
  }

  @Test
  public static class GetOffersFor {

    @Test
    public void delegateToRepository() {
      final Functionality f = make(aMinimalFunctionality());

      final Action a = make(aMinimalAction().withFunctionality(f));

      final Set<RealizedFunctionality> rfs = setOf(realizedFunctionality(f, a));

      final Repository r = mock(Repository.class);
      when(r.findCompatibleOffers(f)).thenReturn(rfs);

      final ComposingFunctionalityProvisionProvider tpp = new ComposingFunctionalityProvisionProvider(r);
      assertEquals(tpp.getOffersFor(f), rfs);
    }

  }

  @Test
  public static class CreateProvision {

    @Test
    public void createFunctionalityProvision() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));
      final Action a = make(aMinimalAction().withFunctionality(f2));
      final FunctionalityProvision fp = make(aFunctionalityProvision()
          .withRequest(f1)
          .withOffer(f2)
          .withProvidingAction(a));
      final ComposingFunctionalityProvisionProvider tpp = new ComposingFunctionalityProvisionProvider(null);
      assertEquals(tpp.createProvision(f1, f2, a), fp);
    }

  }

  @Test
  public static class GetOfferedSubjects {

    @Test
    public void returnRealizedFunctionalities() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));
      final Action a = make(aMinimalAction().withFunctionality(f1, f2));
      final ComposingFunctionalityProvisionProvider tpp = new ComposingFunctionalityProvisionProvider(null);
      assertEquals(tpp.getOfferedSubjects(a), setOf(f1, f2));
    }

  }

}
