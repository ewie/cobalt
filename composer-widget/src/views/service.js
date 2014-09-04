/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var service = require('../service');
var TemplateView = require('./template');

function asInt(s) { return parseInt(s, 10) }

module.exports = TemplateView.extend({

  templateId: 'service',


  events: {
    'change [name=media-type]':                '_change',
    'change [name=goal-mashup]':               '_change',
    'change [name=min-plan-depth]':            '_change',
    'change [name=max-plan-depth]':            '_change',
    'change [name=precursor-composition]':     '_change',
    'change [name=functionality-composition]': '_change',
    'change [name=property-composition]':      '_change',
    'change [name=service-url]':               '_change',
    'submit [name=request]':                   '_submit'
  },


  initialize: function () {
    TemplateView.prototype.initialize.apply(this, arguments);

    this.$mediaType = this.$el.byName('media-type');
    this.$goalMashup = this.$el.byName('goal-mashup');
    this.$serviceUrl = this.$el.byName('service-url');
    this.$minPlanDepth = this.$el.byName('min-plan-depth');
    this.$maxPlanDepth = this.$el.byName('max-plan-depth');
    this.$precursorComposition = this.$el.byName('precursor-composition');
    this.$functionalityComposition = this.$el.byName('functionality-composition');
    this.$propertyComposition = this.$el.byName('property-composition');

    this.$error = this.$el.byName('error');

    this.listenTo(this.model, 'change', this.render);
  },


  render: function () {
    if (this.model.isValid()) {
      this.$goalMashup.setValid();
      this.$serviceUrl.setValid();
      this.$minPlanDepth.setValid();
      this.$maxPlanDepth.setValid();
    } else {
      var err = this.model.validationError;
      this.$goalMashup.setValidity(err.goalMashup);
      this.$serviceUrl.setValidity(err.serviceUrl);
      this.$minPlanDepth.setValidity(err.minPlanDepth);
      this.$maxPlanDepth.setValidity(err.maxPlanDepth);
    }
    var attrs = this.model.attributes;
    this.$mediaType.val(attrs.mediaType);
    this.$goalMashup.val(attrs.goalMashup);
    this.$minPlanDepth.val(attrs.minPlanDepth);
    this.$maxPlanDepth.val(attrs.maxPlanDepth);
    this.$precursorComposition.val(attrs.precursorComposition);
    this.$functionalityComposition.prop('checked', attrs.functionalityComposition);
    this.$propertyComposition.prop('checked', attrs.propertyComposition);
    this.$serviceUrl.val(attrs.serviceUrl);
    return this;
  },


  _change: function () {
    var mediaType = this.$mediaType.val();
    var mashup = this.$goalMashup.val();
    var minPlanDepth = asInt(this.$minPlanDepth.val());
    var maxPlanDepth = asInt(this.$maxPlanDepth.val());
    var precursorComposition = this.$precursorComposition.val();
    var functionalityComposition = this.$functionalityComposition.is(':checked');
    var propertyComposition = this.$propertyComposition.is(':checked');
    var serviceUrl = this.$serviceUrl.val();

    this.model.set({
      mediaType:                mediaType,
      goalMashup:               mashup,
      minPlanDepth:             minPlanDepth,
      maxPlanDepth:             maxPlanDepth,
      precursorComposition:     precursorComposition,
      functionalityComposition: functionalityComposition,
      propertyComposition:      propertyComposition,
      serviceUrl:               serviceUrl
    });
  },


  _submit: function () {
    if (!this.model.isValid()) {
      this.render();
      return false;
    }

    var self = this;
    var err = this.$error;

    var deferred = service.requestPlans(this.model);

    deferred.done(function (plans) {
      err.empty();
      self.trigger('plans', plans);
    });

    deferred.fail(function (r) { err.text(r.message) });

    // prevent default
    return false;
  }


});
