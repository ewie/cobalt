/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var _rave = window.parent.rave;

if (!_rave) {
  console.error("Apache Rave not available");
}


/**
 * @return {any} Rave's current page ID
 */
function getCurrentPageId() {
  return _rave.ui.getCurrentPageId();
}



/**
 * Retrieve a widget from Wookie with a given URI.
 *
 * @param {string} uri a widget URI
 *
 * @return {object} a promise resolving to the matching widget
 */
var getWookieWidget = (function () {
  var cache = {};

  return function getWookieWidget(uri) {
    return $.Deferred.promise(function (dfd) {
      if (uri) {
        if (_.has(cache, uri)) {
          dfd.resolve(cache[uri]);
        } else {
          _rave.api.rpc.getAllWidgets({
            successCallback: function (result) {
              var wookieWidget = _.find(result.result.resultSet, function (wookieWidget) {
                return wookieWidget.url === uri;
              });
              if (wookieWidget) {
                dfd.resolve(wookieWidget);
              } else {
                dfd.reject({ message: "cannot find Rave widget for URI " + uri });
              }
            }
          });
        }
      } else {
        dfd.reject({ message: "cannot determine Rave widget without a URI" });
      }
    });
  };
}());



/**
 * A a widget from Wookie to the current page.
 *
 * @param {object} wookieWidget a widget provided by Wookie
 *
 * @return {object} a promise resolving to the added region widget
 */
function addWookieWidgetToPage(wookieWidget) {
  return $.Deferred.promise(function (dfd) {
    _rave.api.rpc.addWidgetToPage({
      pageId: getCurrentPageId(),
      widgetId: wookieWidget.id,
      successCallback: function (regionWidgetId) {
        renderWidget(regionWidgetId)
          .then(function (r) { dfd.resolve(r) });
      }
    });
  });
}



/**
 * Render a region widget based on a region widget ID.
 *
 * @param {string} regionWidgetId
 *
 * @return {object} a promise resolving to the rendered region widget
 */
function renderWidget(regionWidgetId) {
  return $.Deferred.promise(function (dfd) {
    // XXX rave.renderNewWidget does not provide a callback to notify the
    // caller when the widget has been registered and rendered. Retrieving the
    // resulting region widget is only possible after it has been registered.
    // We could wait a certain amount of time, in the hope that the widget will
    // be rendered in that time frame. A more robust solution is to monkey patch
    // $.fn.load to intercept the call made by _rave.renderNewWidget. Of course
    // this variant is brittle to implementation changes within Rave.

    var _$ = parent.$;
    var _load = parent.$.fn.load;

    _$.fn.load = function (url, callback) {
      _load.call(this, url, function () {
        callback();
        dfd.resolve(getRegionWidget(regionWidgetId));
      });
    };

    // this should invoke $.fn.load synchronously
    _rave.renderNewWidget(regionWidgetId, true, getCurrentPageId());

    // restore $.fn.load, after it was invoked by _rave.renderNewWidget
    _$.fn.load = _load;
  });
}



/**
 * Remove a region widget from the current page. In deatil this means
 * unregistering the region widget and removing its container element from the
 * document.
 *
 * @param {object} regionWidget
 *
 * @return {object} a promise resolving when the region widget has been removed
 */
function removeRegionWidget(regionWidget) {
  return $.Deferred.promise(function (dfd) {
    _rave.api.rpc.removeWidget({
      regionWidgetId: regionWidget.regionWidgetId,
      successCallback: function () {
        // properly unregister the widget
        _rave.unregisterWidget(regionWidget);

        // manually remove the widget element from the DOM
        getRegionWidgetElement(regionWidget).remove();

        dfd.resolve();
      }
    });
  });
}



/**
 * Get all region widgets on the current page except this application's widget.
 *
 * @return {array} all widgets except this application's widget
 */
var getOtherRegionWidgets = (function () {

  function removeHash(url) {
    return url.replace(/(?:#.*)?$/);
  }

  return function getOtherRegionWidgets() {
    var thisUrl = removeHash(location.href);
    return _rave.getWidgets().filter(function (regionWidget) {
      var url = removeHash(regionWidget.widgetUrl);
      return url !== thisUrl;
    });
  };

}());



/**
 * Remove all region widgets on the current page except this application's
 * widget.
 *
 * @return {object} a promise resolving when every region widget has been
 *                  removed
 */
function clearRegionWidgets() {
  var fns = getOtherRegionWidgets()
    .map(function (regionWidget) {
      return function () {
        return removeRegionWidget(regionWidget);
      };
    });
  return $.Deferred.sequence(fns, 200);
}



/**
 * Get the DOM Element wrapping a given region widget.
 *
 * @param {object} regionWidget
 *
 * @return {Element} the DOM Element wrapping `regionWidget`
 */
function getRegionWidgetElement(regionWidget) {
  return regionWidget._el.parentElement;
}



/**
 * Get a region widget by its ID.
 *
 * @param {string} regionWidgetId
 *
 * @return {object} the matching region widget
 */
function getRegionWidget(regionWidgetId) {
  return _rave.getWidget(regionWidgetId);
}


/**
 * Register a function filtering publications made on an OpenAjax Managed Hub.
 *
 * @param {function} filter
 *
 * @see http://www.openajax.org/member/wiki/OpenAjax_Hub_2.0_Specification_Managed_Hub_APIs#OpenAjax.hub.ManagedHub_onPublish_callback_function
 */
var setPublishFilter = (function () {

  var hub = _rave.getManagedHub();

  // remember the original onPublish handler
  var _onPublish = hub._p.onPublish;

  return function setPublishFilter(filter) {
    hub._p.onPublish = function (topic, data, pcont, scont) {
      // invoke the filter only when the original handler allows the publication
      return _onPublish.call(this, topic, data, pcont, scont) &&
             filter(topic, data, pcont, scont);
    };
  };

}());



module.exports = {

  get isEmpty() { return _rave.getWidgets().length <= 1 },

  get pageId() { return getCurrentPageId() },

  setPublishFilter: setPublishFilter,

  getWookieWidget: getWookieWidget,

  getRegionWidgetElement: getRegionWidgetElement,

  addWookieWidgetToPage: addWookieWidgetToPage,

  clearRegionWidgets: clearRegionWidgets

};
