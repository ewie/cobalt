/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Mashup;
import vsr.cobalt.models.Task;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;

/**
 * @author Erik Wienhold
 */
public class MashupMaker implements Maker<Mashup> {

  private final CollectionValue<Task> tasks = new CollectionValue<>();

  public static MashupMaker aMashup() {
    return new MashupMaker();
  }

  public static MashupMaker aMinimalMashup() {
    return aMashup().withTask(aMinimalTask());
  }

  @Override
  public Mashup make() {
    return new Mashup(tasks.asSet());
  }

  public MashupMaker withTask(final Maker<Task> maker) {
    tasks.add(maker);
    return this;
  }

  public MashupMaker withTask(final Task... tasks) {
    this.tasks.addValues(tasks);
    return this;
  }

}
