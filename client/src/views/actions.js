/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var CollectionView = require('./collection');

module.exports = CollectionView.extend({

  tagName: 'ul',

  className: 'actions',

  itemView: require('./action')

});
