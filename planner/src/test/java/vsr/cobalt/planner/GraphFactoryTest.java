/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.providers.FunctionalityProvisionProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.MashupMaker.aMashup;
import static vsr.cobalt.models.makers.MashupMaker.aMinimalMashup;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class GraphFactoryTest {

  @Test
  public static class CreateGraph {

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot realize all mashup functionalities")
    public void rejectWhenNoFunctionalityCanBeRealized() throws Exception {
      final Mashup m = make(aMinimalMashup());

      final GraphFactory gf = new GraphFactory(functionalityProvider(m));
      gf.createGraph(m);
    }

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot realize all mashup functionalities")
    public void rejectWhenSomeFunctionalityCannotBeRealized() throws Exception {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

      final Mashup m = make(aMashup().withFunctionality(f1, f2));

      final FunctionalityProvision fp = make(aFunctionalityProvision()
          .withRequest(f1)
          .withOffer(f1)
          .withProvidingAction(aMinimalAction().withFunctionality(f1)));

      final GraphFactory gf = new GraphFactory(functionalityProvider(m, setOf(fp)));
      gf.createGraph(m);
    }

    @Test
    public void createInitialGraphWhenAllGoalFunctionalitiesCanBeRealized() throws Exception {
      final Functionality f = make(aMinimalFunctionality());
      final Mashup m = make(aMashup().withFunctionality(f));

      final FunctionalityProvision fp = make(aFunctionalityProvision()
          .withRequest(f)
          .withOffer(f)
          .withProvidingAction(aMinimalAction().withFunctionality(f)));

      final GraphFactory gf = new GraphFactory(functionalityProvider(m, setOf(fp)));
      final Graph ig = gf.createGraph(m);

      final InitialLevel il = make(anInitialLevel().withProvision(fp));

      assertEquals(ig.getLastLevel(), il);
    }

    private static FunctionalityProvisionProvider functionalityProvider(final Mashup m,
                                                                        final Set<FunctionalityProvision> fps) {
      final FunctionalityProvisionProvider fp = mock(FunctionalityProvisionProvider.class);
      when(fp.getProvisionsFor(m.getFunctionalities())).thenReturn(fps);
      return fp;
    }

    private static FunctionalityProvisionProvider functionalityProvider(final Mashup m) {
      return functionalityProvider(m, emptySet(FunctionalityProvision.class));
    }

  }

}
