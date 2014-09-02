/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


(function () {

  var date = {};

  function isoDate(d) {
    return d.toISOString().replace(/T.+/, '');
  }

  function publish() {
    if (date.start && date.end && date.start.getTime() <= date.end.getTime()) {
      hub.publish('urn:example:type:DateRange', {
        start: isoDate(date.start),
        end: isoDate(date.end)
      });
    }
  }

  var $start = $('#start');
  var $end = $('#end');

  $start.datepicker({ dateFormat: $.datepicker.ISO_8601 });
  $end.datepicker({ dateFormat: $.datepicker.ISO_8601 });

  $('#start').change(function () {
    date.start = $start.datepicker('getDate');
    publish();
  });

  $('#end').change(function () {
    date.end = $end.datepicker('getDate');
    publish();
  });

}());
