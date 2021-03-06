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
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.PropositionSet;
import vsr.cobalt.models.Widget;
import vsr.cobalt.repository.semantic.Ontology;
import vsr.cobalt.repository.semantic.internalizers.ResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class ActionResourceInternalizer extends ResourceInternalizer<Action> {

  private final ResourceInternalizer<Widget> widgets;

  private final ResourceInternalizer<PropositionSet> propositions;

  private final ResourceInternalizer<Functionality> functionalities;

  private final ResourceInternalizer<Interaction> interactions;

  public ActionResourceInternalizer(final ResourceInternalizer<Widget> widgets,
                                    final ResourceInternalizer<PropositionSet> propositions,
                                    final ResourceInternalizer<Functionality> functionalities,
                                    final ResourceInternalizer<Interaction> interactions) {
    this.widgets = widgets;
    this.propositions = propositions;
    this.functionalities = functionalities;
    this.interactions = interactions;
  }

  @Override
  public Action internalize(final Resource r) {
    final Widget w = widgets.internalize(getSubject(r, Ontology.hasAction));
    final PropositionSet pre = propositions.internalize(getResourceObject(r, Ontology.hasPreConditions));
    final PropositionSet eff = propositions.internalize(getResourceObject(r, Ontology.hasEffects));
    final Set<Functionality> ts = functionalities.internalizeAll(getResourceObjects(r, Ontology.realizesFunctionality));
    final Set<Interaction> is = interactions.internalizeAll(getResourceObjects(r, Ontology.hasInteraction));
    return Action.create(w, pre, eff, ts, is);
  }

}
