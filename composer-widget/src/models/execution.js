/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


module.exports = Backbone.Model.extend({


  initialize: function () {
    this.on('change:steps', this._updateSteps, this);
    this._updateSteps();
  },


  _updateSteps: function () {
    var _steps = this.previous('steps');
    var steps = this.get('steps');
    _steps && _steps.off('change:state', this._state, this);
    steps && steps.on('change:state', this._state, this);
  },


  _state: function (step) {
    if (step.get('state') === 'done') {
      step.set({ expanded: false });
      var nextStep = this.getNextStep(step);
      nextStep && nextStep.set({ state: 'current', expanded: true });
    }
  },


  getCurrentStep: function () {
    return this.get('steps').find(function (step) {
      return step.get('state') === 'current';
    });
  },


  getNextStep: function (step) {
    var steps = this.get('steps');
    for (var i = 0; i < steps.length; i += 1) {
      var _step = steps.at(i);
      if (_step === step) {
        var k = i + 1;
        if (k < steps.length) {
          return steps.at(k);
        }
        break;
      }
    }
  }


});
