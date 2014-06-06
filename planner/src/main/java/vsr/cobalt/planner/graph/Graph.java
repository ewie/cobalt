/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableIterator;

import static java.util.Arrays.asList;

/**
 * A graph is a non-empty sequence of levels, starting with an initial level. The graph can be extended level by level
 * to satisfy actions required by the last level.
 *
 * @author Erik Wienhold
 */
public abstract class Graph {

  // Effectively seal the class by making its constructor private.
  private Graph() {
  }

  /**
   * Create a graph with only an initial level.
   *
   * @param level the initial level
   *
   * @return an extension-less graph
   */
  public static Graph create(final InitialLevel level) {
    return new InitialGraph(level);
  }

  /**
   * Create a graph with an initial level and a sequence of extension levels.
   *
   * @param initialLevel    the initial level
   * @param extensionLevels a sequence of extension levels
   *
   * @return a new graph
   */
  public static Graph create(final InitialLevel initialLevel, final List<ExtensionLevel> extensionLevels) {
    Graph g = create(initialLevel);
    for (final ExtensionLevel xl : extensionLevels) {
      g = g.extendWith(xl);
    }
    return g;
  }

  /**
   * @see #create(InitialLevel, List)
   */
  public static Graph create(final InitialLevel initialLevel, final ExtensionLevel... extensionLevels) {
    return create(initialLevel, asList(extensionLevels));
  }

  /**
   * Extend the graph with an extension level.
   *
   * @param level the extension level
   *
   * @return the extended graph
   */
  public Graph extendWith(final ExtensionLevel level) {
    return new ExtendedGraph(this, level);
  }

  /**
   * @return the number of levels
   */
  public int getDepth() {
    return 1 + getExtensionDepth();
  }

  /**
   * @return true when the graph is extended, false otherwise
   */
  public abstract boolean isExtended();

  /**
   * @return the number of extensions
   */
  public abstract int getExtensionDepth();

  /**
   * @return the initial level
   */
  public abstract InitialLevel getInitialLevel();

  /**
   * @return the graph extended by this graph, null when this graph is an initial graph
   *
   * @throws UnsupportedOperationException when its not an extended graph
   */
  public abstract Graph getBaseGraph();

  /**
   * @return the last level
   */
  public abstract Level getLastLevel();

  /**
   * @return the most recent extension
   */
  public abstract ExtensionLevel getLastExtensionLevel();

  /**
   * Get an extension level via a zero-based index, with {@code 0} identifying the very first extension level.
   *
   * @param index the index of the extension level to retrieve
   *
   * @return the extension level at the given index
   */
  public abstract ExtensionLevel getExtensionLevel(final int index);

  /**
   * Get the sequence of extension levels in extension order, i.e. start with the most recent extension level.
   *
   * @return the sequence of extension levels
   */
  public abstract Iterable<ExtensionLevel> getExtensionLevels();

  @Override
  public abstract int hashCode();

  @Override
  public final boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Graph
        && equals((Graph) other);
  }

  protected abstract boolean equals(final Graph other);

  /**
   * A graph containing only an initial level.
   */
  private static final class InitialGraph extends Graph {

    private final InitialLevel initialLevel;

    public InitialGraph(final InitialLevel initialLevel) {
      this.initialLevel = initialLevel;
    }

    /**
     * @return false
     */
    @Override
    public boolean isExtended() {
      return false;
    }

    /**
     * @return 0
     */
    @Override
    public int getExtensionDepth() {
      return 0;
    }

    @Override
    public InitialLevel getInitialLevel() {
      return initialLevel;
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public Graph getBaseGraph() {
      throw new UnsupportedOperationException();
    }

    /**
     * @return {@link #getInitialLevel()}
     */
    @Override
    public InitialLevel getLastLevel() {
      return getInitialLevel();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public ExtensionLevel getLastExtensionLevel() {
      throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public ExtensionLevel getExtensionLevel(final int index) {
      throw new UnsupportedOperationException();
    }

    /**
     * @return an empty iterable
     */
    @Override
    public Iterable<ExtensionLevel> getExtensionLevels() {
      return Collections.emptyList();
    }

    @Override
    public int hashCode() {
      return initialLevel.hashCode();
    }

    @Override
    protected boolean equals(final Graph other) {
      return other instanceof InitialGraph
          && equals((InitialGraph) other);
    }

    private boolean equals(final InitialGraph other) {
      return getInitialLevel().equals(other.getInitialLevel());
    }

  }

  /**
   * A graph extending another graph.
   */
  private static final class ExtendedGraph extends Graph {

    private final Graph graph;

    private final ExtensionLevel extensionLevel;

    private final int extensionDepth;

    private final int hashCode;

    public ExtendedGraph(final Graph graph, final ExtensionLevel extensionLevel) {
      if (!extensionLevel.canExtendOn(graph.getLastLevel())) {
        throw new IllegalArgumentException("expecting a sufficient extension");
      }
      this.graph = graph;
      this.extensionLevel = extensionLevel;
      extensionDepth = graph.getDepth();
      hashCode = Objects.hash(graph, extensionLevel);
    }

    /**
     * @return true
     */
    @Override
    public boolean isExtended() {
      return true;
    }

    @Override
    public ExtensionLevel getExtensionLevel(final int index) {
      return Iterables.get(getExtensionLevels(), getExtensionDepth() - index - 1);
    }

    @Override
    public InitialLevel getInitialLevel() {
      Graph g = graph;
      while (g.isExtended()) {
        g = g.getBaseGraph();
      }
      return g.getInitialLevel();
    }

    @Override
    public Graph getBaseGraph() {
      return graph;
    }

    /**
     * @return non-zero extension depth
     */
    @Override
    public int getExtensionDepth() {
      return extensionDepth;
    }

    /**
     * @return a sequence of one or more extensions
     */
    @Override
    public Iterable<ExtensionLevel> getExtensionLevels() {
      return new ExtensionLevelIterable(this);
    }

    /**
     * @return {@link #getLastExtensionLevel()}
     */
    @Override
    public ExtensionLevel getLastLevel() {
      return getLastExtensionLevel();
    }

    /**
     * @return the extension level used by this graph
     */
    @Override
    public ExtensionLevel getLastExtensionLevel() {
      return extensionLevel;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    protected boolean equals(final Graph other) {
      return other instanceof ExtendedGraph
          && equals((ExtendedGraph) other);
    }

    private boolean equals(final ExtendedGraph other) {
      return extensionDepth == other.extensionDepth
          && getInitialLevel().equals(other.getInitialLevel())
          && Iterables.elementsEqual(getExtensionLevels(), other.getExtensionLevels());
    }

    private static class ExtensionLevelIterable implements Iterable<ExtensionLevel> {

      private final ExtendedGraph graph;

      public ExtensionLevelIterable(final ExtendedGraph graph) {
        this.graph = graph;
      }

      @Override
      public ExtensionLevelIterator iterator() {
        return new ExtensionLevelIterator(graph);
      }

    }

    private static class ExtensionLevelIterator extends UnmodifiableIterator<ExtensionLevel> {

      private Graph graph;

      public ExtensionLevelIterator(final ExtendedGraph graph) {
        this.graph = graph;
      }

      @Override
      public boolean hasNext() {
        return graph.isExtended();
      }

      @Override
      public ExtensionLevel next() {
        final ExtensionLevel xl = graph.getLastExtensionLevel();
        graph = graph.getBaseGraph();
        return xl;
      }

    }

  }

}
