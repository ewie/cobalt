/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

import java.util.Set;

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
import static vsr.cobalt.models.makers.PropertyMaker.aProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ComposingPropertyProvisionProviderTest {

  @Test
  public void extendsComposingProvisionProvider() {
    assertSubClass(ComposingPropertyProvisionProvider.class, ComposingProvisionProvider.class);
  }

  private static PublishedProperty publishedProperty(final Property p, final Action a) {
    return new PublishedProperty(p, a);
  }

  @Test
  public static class GetOffersFor {

    @Test
    public void delegateToRepository() {
      final Property p = make(aMinimalProperty());

      final Action a = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withFilled(p)));

      final Set<PublishedProperty> pubs = setOf(publishedProperty(p, a));

      final Repository r = mock(Repository.class);
      when(r.findCompatibleProperties(p)).thenReturn(pubs);

      final ComposingPropertyProvisionProvider ppp = new ComposingPropertyProvisionProvider(r);
      assertEquals(ppp.getOffersFor(p), pubs);
    }

  }

  @Test
  public static class CreateProvision {

    @Test
    public void createPropertyProvision() {
      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));
      final Action a = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withFilled(p2)));
      final PropertyProvision pp = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p2)
          .withProvidingAction(a));
      final ComposingPropertyProvisionProvider ppp = new ComposingPropertyProvisionProvider(null);
      assertEquals(ppp.createProvision(p1, p2, a), pp);
    }

  }

  @Test
  public static class GetOfferedSubjects {

    @Test
    public void returnPublishedProperties() {
      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));
      final Action a = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withFilled(p1, p2)));
      final ComposingPropertyProvisionProvider ppp = new ComposingPropertyProvisionProvider(null);
      assertEquals(ppp.getOfferedSubjects(a), setOf(p1, p2));
    }

  }

}
