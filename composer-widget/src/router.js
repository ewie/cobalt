/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var queryString = require('./util/queryString');



function routeToRegExp(route) {
  return Backbone.Router.prototype._routeToRegExp.call({}, route);
}



function extractParams(pattern, fragment) {
  return Backbone.Router.prototype._extractParameters.call({}, pattern, fragment);
}



// OpenAjax uses the URL fragment to inject data
//   (https://issues.apache.org/jira/browse/RAVE-772).
// This complicates the usage of URL fragments as means of controlling an
// application's state (as realized by Backbone's router).

var Router = module.exports = function Router(options) {

  var self = this;
  var routes = options.routes;

  var key = this.key = options.key;

  Object.getOwnPropertyNames(routes).forEach(function (route) {
    var pattern = routeToRegExp(route);
    var test = {
      test: function (fragment) {
        var value = queryString.get(fragment, key) || '';
        return pattern.test(value);
      }
    };
    var name = routes[route];
    Backbone.history.route(test, function (fragment) {
      var value = queryString.get(fragment, key) || '';
      var args = extractParams(pattern, value);
      self.trigger.apply(self, ['route:' + name].concat(args));
      self.trigger('route', name, args);
      Backbone.history.trigger('route', self, name, args);
    });
  });

};



_.extend(Router.prototype, Backbone.Events, {

  navigate: function (value, options) {
    var fragment = queryString.set(Backbone.history.getHash(), this.key, value);
    Backbone.history.navigate(fragment, options);
    return this;
  }

});
