/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * @author Erik Wienhold
 */
public final class Ontology {

  public static final String NAMESPACE = "https://vsr.informatik.tu-chemnitz.de/projects/2014/cobalt/mashup#";

  public static final Resource Action = resource("Action");
  public static final Resource Functionality = resource("Functionality");
  public static final Resource Interaction = resource("Interaction");
  public static final Resource Mashup = resource("Mashup");
  public static final Resource Property = resource("Property");
  public static final Resource Propositions = resource("Propositions");
  public static final Resource Widget = resource("Widget");

  public static final Property clearValueOf = property("clearValueOf");
  public static final Property fillValueOf = property("fillValueOf");
  public static final Property hasAction = property("hasAction");
  public static final Property hasEffects = property("hasEffects");
  public static final Property hasInstructionText = property("hasInstructionText");
  public static final Property hasInteraction = property("hasInteraction");
  public static final Property hasName = property("hasName");
  public static final Property hasPreConditions = property("hasPreConditions");
  public static final Property hasPublicProperty = property("hasPublicProperty");
  public static final Property hasType = property("hasType");
  public static final Property hasWidget = property("hasWidget");
  public static final Property realizesFunctionality = property("realizesFunctionality");
  public static final Property subFunctionalityOf = property("subFunctionalityOf");

  private static Resource resource(final String localName) {
    return ResourceFactory.createResource(uriref(localName));
  }

  private static Property property(final String localName) {
    return ResourceFactory.createProperty(uriref(localName));
  }

  private static String uriref(final String localName) {
    return NAMESPACE + localName;
  }

}
