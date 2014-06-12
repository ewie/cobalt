/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Type;
import vsr.cobalt.models.Widget;

import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aProperty;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.models.makers.TypeMaker.aType;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.testing.Utilities.make;

/**
 * @author Erik Wienhold
 */
public final class Models {

  public static Widget widget(final int id) {
    return make(aWidget().withIdentifier("urn:example:widget:" + id));
  }

  public static Action action(final Widget w, final Property p) {
    return make(aMinimalAction()
        .withWidget(w)
        .withPub(p));
  }

  public static Action action(final Widget w, final Task t) {
    return make(aMinimalAction()
        .withWidget(w)
        .withTask(t));
  }

  public static Type type(final int id) {
    return make(aType().withIdentifier("urn:example:type:" + id));
  }

  public static Task task(final int id) {
    return make(aTask().withIdentifier("urn:example:task:" + id));
  }

  public static Property property(final int id, final Type type) {
    return make(aProperty()
        .withName("p" + id)
        .withType(type));
  }

}
