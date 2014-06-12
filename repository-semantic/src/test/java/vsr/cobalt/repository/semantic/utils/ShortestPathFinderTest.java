/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.testng.Assert.assertEquals;

@Test
public class ShortestPathFinderTest {

  @Test
  public void handleCycles() {
    final Resource source = ResourceFactory.createResource();
    final Resource target = ResourceFactory.createResource();
    final Property p = ResourceFactory.createProperty("urn:example:p");

    final Model m = ModelFactory.createDefaultModel();
    m.add(source, p, target);
    m.add(target, p, source);

    final ShortestPathFinder finder = new ShortestPathFinder(m, p);
    assertEquals(finder.findShortestPath(source, target), asList(source, target));
  }

  @Test
  public static class WhenSourceEqualsTarget {

    Resource source;

    ShortestPathFinder finder;

    @BeforeMethod
    public void setUp() {
      source = ResourceFactory.createResource();
      final Resource target = ResourceFactory.createResource();
      final Property p = ResourceFactory.createProperty("urn:example:p");

      final Model model = ModelFactory.createDefaultModel();
      model.add(source, p, target);

      finder = new ShortestPathFinder(model, p);
    }

    @Test
    public void findShortestPath() {
      assertEquals(finder.findShortestPath(source, source), asList(source));
    }

    @Test
    public void findShortestPathLength() {
      assertEquals(finder.findShortestPathLength(source, source), 0);
    }

  }

  @Test
  public static class WhenPathExists {

    Resource source;

    Resource target;

    Resource mid;

    ShortestPathFinder finder;

    @BeforeMethod
    public void setUp() {
      source = ResourceFactory.createResource();
      target = ResourceFactory.createResource();

      mid = ResourceFactory.createResource();
      final Resource y = ResourceFactory.createResource();

      final Property p = ResourceFactory.createProperty("urn:example:p");
      final Model model = ModelFactory.createDefaultModel();

      // a path of length 3
      model.add(source, p, mid);
      model.add(mid, p, y);
      model.add(y, p, target);

      // a shorter path of length 2
      model.add(source, p, mid);
      model.add(mid, p, target);

      finder = new ShortestPathFinder(model, p);
    }

    @Test
    public void findShortestPath() {
      assertEquals(finder.findShortestPath(source, target), asList(source, mid, target));
    }

    @Test
    public void findShortestPathLength() {
      assertEquals(finder.findShortestPathLength(source, target), 2);
    }

  }

  @Test
  public static class WhenNoPathExists {

    Resource source;

    Resource target;

    ShortestPathFinder finder;

    @BeforeMethod
    public void setUp() {
      source = ResourceFactory.createResource();
      target = ResourceFactory.createResource();

      final Property p = ResourceFactory.createProperty("urn:example:p");
      final Resource x = ResourceFactory.createResource();
      final Resource y = ResourceFactory.createResource();

      final Model model = ModelFactory.createDefaultModel();
      model.add(source, p, x);
      model.add(target, p, y);

      finder = new ShortestPathFinder(model, p);
    }

    @Test
    public void findShortestPath() {
      assertEquals(finder.findShortestPath(source, target), emptyList());
    }

    @Test
    public void findShortestPathLength() {
      assertEquals(finder.findShortestPathLength(source, target), -1);
    }

  }

}
