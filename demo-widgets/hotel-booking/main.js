/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


(function () {

  function subscribe(topic, fn) {
    hub.subscribe(topic, function (topic, data) {
      fn(data);
    });
  }

  function changeHotel(hotel) {
    var name = document.getElementById('hotel');
    var address = document.getElementById('address');

    name.value = hotel.name;
    address.value = hotel.address.formatted.split(/\s*,\s*/).join('\n');
  }

  function changeDate(dateRange) {
    var start = document.getElementById('start-date');
    var end = document.getElementById('end-date');

    start.value = dateRange.start;
    end.value = dateRange.end;
  }

  function changePayment(payment) {
    var creditCard = document.getElementById('credit-card');
    var expDate = document.getElementById('expiration-date');
    var holder = document.getElementById('holder');

    var year = payment.expirationDate.year;
    var month = payment.expirationDate.month;

    creditCard.value = payment.cardNumber;
    expDate.value = formatExpirationDate(year, month);
    holder.value = payment.holderName;
  }

  function formatExpirationDate(year, month) {
    var y = (year % 100).toString(10);
    var m = month.toString(10);
    if (month < 10) {
      m = '0' + m;
    }
    return y + m;
  }

  function padLines(s, padlen) {
    var ws = '';
    while (ws.length < padlen) {
      ws += ' ';
    }

    var lines = s.split('\n');
    var paddedLines = [ lines[0] ];
    for (var i = 1; i < lines.length; i += 1) {
      paddedLines.push(ws + lines[i]);
    }
    return paddedLines.join('\n');
  }

  subscribe('urn:example:type:Hotel', changeHotel);
  subscribe('urn:example:type:DateRange', changeDate);
  subscribe('urn:example:type:CreditCard', changePayment);

  document.getElementById('booking').addEventListener('submit', function (ev) {
    var text = 'Booking Details:\n'
             + 'Hotel:          ' + document.getElementById('hotel').value + '\n'
             + 'Address:        ' + padLines(document.getElementById('address').value, 'Address:        '.length) + '\n'
             + 'Date:\n'
             + '  From:         ' + document.getElementById('start-date').value + '\n'
             + '  To:           ' + document.getElementById('end-date').value + '\n'
             + 'No. of People:  ' + document.getElementById('number-of-people').value + '\n'
             + 'Payment:\n'
             + '  Credit Card:  ' + document.getElementById('credit-card').value + '\n'
             + '  Exp. Date:    ' + document.getElementById('expiration-date').value + '\n'
             + '  Card Holder:  ' + document.getElementById('holder').value + '\n'
    document.getElementById('response').textContent = text;
    ev.preventDefault();
  });

}());
