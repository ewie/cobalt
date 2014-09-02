/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


(function () {

  var numberEl = document.getElementById('number');
  var expDateEl = document.getElementById('exp-date');
  var holderEl = document.getElementById('holder');

  function getCreditCardData() {
    return {
      cardNumber: numberEl.value,
      expirationDate: parseExpDate(expDateEl.value),
      holderName: holderEl.value
    };
  }

  function parseExpDate(s) {
    var match = /^(\d{2})(\d{2})$/.exec(s);
    if (match) {
      return {
        year: 2000 + parseInt(match[1], 10),
        month: parseInt(match[2], 10)
      };
    }
  }

  function validateCreditCard(data) {
    var errors = Object.create(null);
    if (!isValidNumber(data.cardNumber)) {
      errors.number = "invalid credit card number";
    }
    if (!isValidExpirationDate(data.expirationDate)) {
      errors.expDate = "invalid credit card expiration date";
    }
    if (!isValidHolder(data.holderName)) {
      errors.holder = "invalid credit card holder name";
    }
    for (var _ in errors) {
      return errors;
    }
  }

  function isValidNumber(number) {
    if (!/^\d+$/.test(number)) {
      return false;
    }
    var sum = number.split('').reduce(function (sum, d, i) {
      var v = parseInt(d, 10);
      var x = 0;
      if (i % 2 === 1) {
        v *= 2;
        if (v > 9) {
          x += 1;
        }
      }
      return sum + x + (v % 10);
    }, 0);
    return sum % 10 === 0;
  }

  function isValidExpirationDate(expDate) {
    if (!expDate) {
      return false;
    }
    var now = new Date();
    var y = now.getFullYear();
    var m = now.getMonth();
    var xy = expDate.year;
    var xm = expDate.month;
    return (xy > y || (xy === y && xm >= m));
  }

  function isValidHolder(holder) {
    return !/^\s*$/.test(holder);
  }

  function verifyCreditCard() {
    // clear errors
    numberEl.nextSibling.textContent = '';
    expDateEl.nextSibling.textContent = '';
    holderEl.nextSibling.textContent = '';

    var data = getCreditCardData();
    var errors = validateCreditCard(data);

    if (errors) {
      errors.number && (numberEl.nextSibling.textContent = errors.number);
      errors.expDate && (expDateEl.nextSibling.textContent = errors.expDate);
      errors.holder && (holderEl.nextSibling.textContent = errors.holder);
    } else {
      console.log('credit card', data);
      hub.publish('urn:example:type:CreditCard', data);
    }
  }

  document.getElementById('credit-card').addEventListener('submit', function (ev) {
    verifyCreditCard();
    ev.preventDefault();
  });

}());
