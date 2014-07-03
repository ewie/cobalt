/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


function setRelativeRating(plans) {
  if (plans.length > 0) {
    var minRating = plans[0].get('rating');
    var maxRating = plans[plans.length - 1].get('rating');
    plans.forEach(function (plan) {
      plan.set({ relativeRating: 1 - ((plan.get('rating') - minRating) / (maxRating - minRating)) });
    });
  }
}



module.exports = Backbone.Collection.extend({

  model: require('./plan'),

  comparator: 'rating',

  set: function () {
    var result = Backbone.Collection.prototype.set.apply(this, arguments);
    setRelativeRating(this.models);
    return result;
  }

});
