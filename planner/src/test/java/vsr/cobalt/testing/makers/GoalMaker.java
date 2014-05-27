/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.makers;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.planner.Goal;
import vsr.cobalt.planner.models.Task;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public class GoalMaker implements Maker<Goal> {

  private final CollectionValue<Task> tasks = new CollectionValue<>();

  public static GoalMaker aGoal() {
    return new GoalMaker();
  }

  @Override
  public Goal make() {
    return new Goal(ImmutableSet.copyOf(tasks.get()));
  }

  public GoalMaker withTask(final Maker<Task> maker) {
    tasks.add(maker);
    return this;
  }

  public GoalMaker withTask(final Task... tasks) {
    this.tasks.addValues(tasks);
    return this;
  }

}
