/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.TaskProvision;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.MashupMaker.aMashup;
import static vsr.cobalt.models.makers.MashupMaker.aMinimalMashup;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class GraphFactoryTest {

  @Test
  public static class CreateGraph {

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot realize all mashup tasks")
    public void rejectWhenNoTaskCannotBeRealized() throws Exception {
      final Mashup m = make(aMinimalMashup());

      final GraphFactory gf = new GraphFactory(taskProvider(m));
      gf.createGraph(m);
    }

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot realize all mashup tasks")
    public void rejectWhenSomeTaskCannotBeRealized() throws Exception {
      final Task t1 = make(aTask().withIdentifier("t1"));
      final Task t2 = make(aTask().withIdentifier("t2"));

      final Mashup m = make(aMashup().withTask(t1, t2));

      final TaskProvision tp = make(aTaskProvision()
          .withRequest(t1)
          .withOffer(t1)
          .withProvidingAction(aMinimalAction().withTask(t1)));

      final GraphFactory gf = new GraphFactory(taskProvider(m, setOf(tp)));
      gf.createGraph(m);
    }

    @Test
    public void createInitialGraphWhenAllGoalTasksCanBeRealized() throws Exception {
      final Task t = make(aMinimalTask());
      final Mashup m = make(aMashup().withTask(t));

      final TaskProvision tp = make(aTaskProvision()
          .withRequest(t)
          .withOffer(t)
          .withProvidingAction(aMinimalAction().withTask(t)));

      final GraphFactory gf = new GraphFactory(taskProvider(m, setOf(tp)));
      final Graph ig = gf.createGraph(m);

      final InitialLevel il = make(anInitialLevel().withTaskProvision(tp));

      assertEquals(ig.getLastLevel(), il);
    }

    private static TaskProvisionProvider taskProvider(final Mashup m, final Set<TaskProvision> tps) {
      final TaskProvisionProvider tp = mock(TaskProvisionProvider.class);
      when(tp.getProvisionsFor(m.getTasks())).thenReturn(tps);
      return tp;
    }

    private static TaskProvisionProvider taskProvider(final Mashup m) {
      return taskProvider(m, emptySet(TaskProvision.class));
    }

  }

}
