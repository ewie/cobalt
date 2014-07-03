/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var StepsView = require('./steps');
var TemplateView = require('./template');

module.exports = TemplateView.extend({

  templateId: 'execution',

  events: {
    'click [name=reload]':       '_reload',
    'click [name=expand-all]':   '_expandAll',
    'click [name=collapse-all]': '_collapseAll'
  },


  initialize: function () {
    TemplateView.prototype.initialize.apply(this, arguments);

    this.$steps = this.$el.byName('steps');

    this.listenTo(this.model, 'change:steps', this._steps);
  },


  _steps: function () {
    var v = new StepsView({
      el: this.$steps,
      collection: this.model.attributes.steps
    });
    v.render();
  },


  _reload: function () {
    this.model.trigger('reload');
  },


  _expandAll: function () {
    this.model.get('steps').forEach(function (step) {
      step.set({ expanded: true });
    });
  },


  _collapseAll: function () {
    this.model.get('steps').forEach(function (step) {
      if (step.get('state') !== 'current') {
        step.set({ expanded: false });
      }
    });
  }


});
