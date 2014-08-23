/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var templates = require('../templates');

module.exports = Backbone.View.extend({

  initialize: function () {
    this.$el.html(templates.get(this.templateId));
  }

});
