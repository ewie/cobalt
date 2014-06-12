/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Finds the shortest path of a defined property between two resources of a model. The length of the path is
 * determined by the number of edges, i.e. each edge has weight 1.
 *
 * @author Erik Wienhold
 */
public class ShortestPathFinder {

  private final Model model;

  private final Property property;

  /**
   * @param model    a model
   * @param property a property identifying the kind of paths to find
   */
  public ShortestPathFinder(final Model model, final Property property) {
    this.model = model;
    this.property = property;
  }

  /**
   * Find the shortest path between a source and target resource.
   *
   * @param source the source resource
   * @param target the target resource
   *
   * @return a list containing each resource on the path, empty list when no path exists
   */
  public List<Resource> findShortestPath(final Resource source, final Resource target) {
    return discoverPath(source, target).extract(target);
  }

  /**
   * Find the shortest path's length between a source and target resource.
   *
   * @param source the source resource
   * @param target the target resource
   *
   * @return the number of edges on the path, -1 when no path exists
   */
  public int findShortestPathLength(final Resource source, final Resource target) {
    return discoverPath(source, target).getLength(target);
  }

  private Path discoverPath(final Resource source, final Resource target) {
    if (source.equals(target)) {
      return Path.empty(source);
    }

    final Map<Resource, Resource> precursors = new HashMap<>();
    final DistanceMap dist = new DistanceMap(source);
    final DistanceQueue queue = new DistanceQueue(source);

    while (!queue.isEmpty()) {
      final Resource u = queue.dequeue();

      if (u.equals(target)) {
        return Path.some(precursors);
      }

      final Iterator<Resource> ns = getNeighbours(u);

      while (ns.hasNext()) {
        final Resource v = ns.next();
        final int alt = dist.get(u) + 1;
        if (alt < dist.get(v)) {
          queue.updatePriority(v, dist.get(v), alt);
          dist.set(v, alt);
          precursors.put(v, u);
        }
      }
    }

    return Path.none();
  }

  private Iterator<Resource> getNeighbours(final Resource node) {
    return new NodeToResourceMapper(model.listObjectsOfProperty(node, property));
  }

  private static abstract class Path {

    public static Path none() {
      return NoPath.INSTANCE;
    }

    public static Path empty(final Resource node) {
      return new EmptyPath(node);
    }

    public static Path some(final Map<Resource, Resource> precursors) {
      return new SomePath(precursors);
    }

    public abstract List<Resource> extract(Resource target);

    public int getLength(final Resource target) {
      return extract(target).size() - 1;
    }

  }

  private static class EmptyPath extends Path {

    private final Resource node;

    public EmptyPath(final Resource node) {
      this.node = node;
    }

    @Override
    public List<Resource> extract(final Resource target) {
      return isSameNode(target)
          ? ImmutableList.of(target)
          : Collections.<Resource>emptyList();
    }

    @Override
    public int getLength(final Resource target) {
      return isSameNode(target) ? 0 : -1;
    }

    private boolean isSameNode(final Resource node) {
      return this.node.equals(node);
    }

  }

  private static class NoPath extends Path {

    public static final NoPath INSTANCE = new NoPath();

    private NoPath() {
    }

    @Override
    public List<Resource> extract(final Resource target) {
      return Collections.emptyList();
    }

    @Override
    public int getLength(final Resource target) {
      return -1;
    }

  }

  private static class SomePath extends Path {

    private final Map<Resource, Resource> precursors;

    public SomePath(final Map<Resource, Resource> precursors) {
      this.precursors = precursors;
    }

    @Override
    public List<Resource> extract(final Resource target) {
      final List<Resource> path = new LinkedList<>();
      Resource u = target;
      if (contains(u)) {
        path.add(0, u);
        do {
          u = get(u);
          path.add(0, u);
        } while (contains(u));
      }
      return path;
    }

    @Override
    public int getLength(final Resource target) {
      int len = 0;
      Resource u = target;
      while (contains(u)) {
        len += 1;
        u = get(u);
      }
      return len;
    }

    private boolean contains(final Resource node) {
      return precursors.containsKey(node);
    }

    private Resource get(final Resource node) {
      return precursors.get(node);
    }

  }

  private static class DistanceMap {

    private static final int INF = Integer.MAX_VALUE;

    private final Map<Resource, Integer> distances = new HashMap<>();

    public DistanceMap(final Resource source) {
      set(source, 0);
    }

    public void set(final Resource node, final int distance) {
      distances.put(node, distance);
    }

    public int get(final Resource node) {
      return valueOrInf(distances.get(node));
    }

    private static int valueOrInf(final Integer value) {
      return value == null ? INF : value;
    }

  }

  private static class DistanceQueue {

    private final PriorityQueue<Item> queue = new PriorityQueue<>();

    public DistanceQueue(final Resource source) {
      enqueue(source, 0);
    }

    public boolean isEmpty() {
      return queue.isEmpty();
    }

    public void enqueue(final Resource node, final int priority) {
      queue.offer(item(node, priority));
    }

    public Resource dequeue() {
      return queue.poll().node;
    }

    public void updatePriority(final Resource node, final int oldPriority, final int newPriority) {
      queue.remove(item(node, oldPriority));
      queue.offer(item(node, newPriority));
    }

    private static Item item(final Resource node, final int priority) {
      return new Item(node, priority);
    }

    private static class Item implements Comparable<Item> {

      private final Resource node;

      private final int priority;

      public Item(final Resource node, final int priority) {
        this.node = node;
        this.priority = priority;
      }

      @Override
      public int compareTo(final Item other) {
        return Integer.compare(priority, other.priority);
      }

    }

  }

}
