/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.makers;

import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.testing.makers.InitialLevelMaker.aMinimalInitialLevel;

/**
 * @author Erik Wienhold
 */
public class GraphMaker implements Maker<Graph> {

  private final AtomicValue<InitialLevel> initial = new AtomicValue<>();

  private final CollectionValue<ExtensionLevel> extensions = new CollectionValue<>();

  public static GraphMaker aGraph() {
    return new GraphMaker();
  }

  public static GraphMaker aMinimalGraph() {
    return aGraph().withInitialLevel(aMinimalInitialLevel());
  }

  @Override
  public Graph make() {
    if (extensions.isEmpty()) {
      return Graph.create(initial.get());
    } else {
      return Graph.create(initial.get(), extensions.asList());
    }
  }

  public GraphMaker withInitialLevel(final InitialLevel level) {
    initial.set(level);
    return this;
  }

  public GraphMaker withInitialLevel(final Maker<InitialLevel> maker) {
    initial.set(maker);
    return this;
  }

  public GraphMaker withExtensionLevel(final ExtensionLevel... extensions) {
    this.extensions.addValues(extensions);
    return this;
  }

  public GraphMaker withExtensionLevel(final Maker<ExtensionLevel> maker) {
    extensions.add(maker);
    return this;
  }

}
