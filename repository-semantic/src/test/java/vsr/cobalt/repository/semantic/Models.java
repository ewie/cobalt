/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import java.net.URI;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Type;
import vsr.cobalt.models.Widget;

import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.TypeMaker.aType;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.testing.Utilities.make;

/**
 * @author Erik Wienhold
 */
public final class Models {

  public static Widget widget(final int id) {
    return make(aWidget().withIdentifier(uri("urn:example:widget:" + id)));
  }

  public static Type type(final int id) {
    return make(aType().withIdentifier(uri("urn:example:type:" + id)));
  }

  public static Functionality functionality(final int id) {
    return make(aFunctionality().withIdentifier(uri("urn:example:fn:" + id)));
  }

  public static Action action(final Widget w, final Property p) {
    return make(aMinimalAction()
        .withWidget(w)
        .withEffects(aPropositionSet()
            .withFilled(p)));
  }

  public static Action action(final Widget w, final Functionality t) {
    return make(aMinimalAction()
        .withWidget(w)
        .withFunctionality(t));
  }

  public static Property property(final int id, final Type type) {
    return make(aProperty()
        .withName("p" + id)
        .withType(type));
  }

  private static URI uri(final String s) {
    return URI.create(s);
  }

}
