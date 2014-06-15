/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PropositionSet;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Widget;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class ActionResourceInternalizer extends ResourceInternalizer<Action> {

  private final ResourceInternalizer<Widget> widgets;

  private final ResourceInternalizer<PropositionSet> propositions;

  private final ResourceInternalizer<Property> properties;

  private final ResourceInternalizer<Task> tasks;

  private final ResourceInternalizer<Interaction> interactions;

  public ActionResourceInternalizer(final ResourceInternalizer<Widget> widgets,
                                    final ResourceInternalizer<PropositionSet> propositions,
                                    final ResourceInternalizer<Property> properties,
                                    final ResourceInternalizer<Task> tasks,
                                    final ResourceInternalizer<Interaction> interactions) {
    this.widgets = widgets;
    this.propositions = propositions;
    this.properties = properties;
    this.tasks = tasks;
    this.interactions = interactions;
  }

  @Override
  public Action internalize(final Resource r) {
    final Widget w = widgets.internalize(getSubject(r, Ontology.hasAction));
    final PropositionSet pre = propositions.internalize(getResourceObject(r, Ontology.hasPreConditions));
    final PropositionSet eff = propositions.internalize(getResourceObject(r, Ontology.hasEffects));
    final Set<Property> ps = properties.internalizeAll(getResourceObjects(r, Ontology.publishesValueOf));
    final Set<Task> ts = tasks.internalizeAll(getResourceObjects(r, Ontology.realizesTask));
    final Set<Interaction> is = interactions.internalizeAll(getResourceObjects(r, Ontology.hasInteraction));
    return Action.create(w, pre, eff, ps, ts, is);
  }

}
