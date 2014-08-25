/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var TemplateView = require('./template');

module.exports = TemplateView.extend({

  tagName: 'li',

  className: 'interaction',

  templateId: 'interaction',


  initialize: function () {
    TemplateView.prototype.initialize.apply(this, arguments);
    this.$instruction = this.$el.byName('instruction-text');
  },


  render: function () {
    this.$instruction.text(this.model.attributes.instructionText);
  }


});
