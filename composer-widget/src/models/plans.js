/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


function setRelativeRating(plans) {
  if (plans.length > 0) {
    // Plans are ordered by rating, therefore we know the minimum and maximum
    // without searching.
    var min = plans[0].get('rating');
    var max = plans[plans.length - 1].get('rating');

    var dr = max - min;

    if (dr === 0) {
      // The min and max may be zero causing division by zero.
      // In this case, however, every plan has equal rating, thus a relative
      // rating of 100%.
      plans.forEach(function (plan) {
        plan.set({ relativeRating: 1 });
      });
    } else {
      plans.forEach(function (plan) {
        var r = plan.get('rating');
        var rr = 1 - ((r - min) / dr);
        plan.set({ relativeRating: rr });
      });
    }
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
