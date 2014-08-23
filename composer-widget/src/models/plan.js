/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var seqid = require('../util/seqid');

module.exports = Backbone.Model.extend({

  initialize: function () {
    this.attributes.id || (this.set('id', seqid.nextId()));
  }

});
