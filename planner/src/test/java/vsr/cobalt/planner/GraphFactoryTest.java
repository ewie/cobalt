/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Task;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.GoalMaker.aGoal;
import static vsr.cobalt.testing.makers.GoalMaker.aMinimalGoal;
import static vsr.cobalt.testing.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.makers.TaskMaker.aTask;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aTaskProvision;

@Test
public class GraphFactoryTest {

  @Test
  public static class CreateGraph {

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot realize all goal tasks")
    public void rejectWhenNoTaskCannotBeRealized() throws Exception {
      final Goal g = make(aMinimalGoal());

      final GraphFactory gf = new GraphFactory(taskProvider(g));
      gf.createGraph(g);
    }

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot realize all goal tasks")
    public void rejectWhenSomeTaskCannotBeRealized() throws Exception {
      final Task t1 = make(aTask().withIdentifier("t1"));
      final Task t2 = make(aTask().withIdentifier("t2"));

      final Goal g = make(aGoal().withTask(t1, t2));

      final TaskProvision tp = make(aTaskProvision()
          .withRequest(t1)
          .withOffer(t1)
          .withProvidingAction(aMinimalAction().withTask(t1)));

      final GraphFactory gf = new GraphFactory(taskProvider(g, setOf(tp)));
      gf.createGraph(g);
    }

    @Test
    public void createInitialGraphWhenAllGoalTasksCanBeRealized() throws Exception {
      final Task t = make(aMinimalTask());
      final Goal g = make(aGoal().withTask(t));

      final TaskProvision tp = make(aTaskProvision()
          .withRequest(t)
          .withOffer(t)
          .withProvidingAction(aMinimalAction().withTask(t)));

      final GraphFactory gf = new GraphFactory(taskProvider(g, setOf(tp)));
      final Graph ig = gf.createGraph(g);

      final InitialLevel il = make(anInitialLevel().withTaskProvision(tp));

      assertEquals(ig.getLastLevel(), il);
    }

    private static TaskProvisionProvider taskProvider(final Goal g, final Set<TaskProvision> tps) {
      final TaskProvisionProvider tp = mock(TaskProvisionProvider.class);
      when(tp.getProvisionsFor(g.getTasks())).thenReturn(tps);
      return tp;
    }

    private static TaskProvisionProvider taskProvider(final Goal g) {
      return taskProvider(g, emptySet(TaskProvision.class));
    }

  }

}
