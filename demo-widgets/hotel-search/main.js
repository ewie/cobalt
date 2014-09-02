/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


// Adapted from
// https://developers.google.com/maps/documentation/javascript/examples/places-autocomplete-hotelsearch

(function () {

  var MARKER_PATH = 'https://maps.gstatic.com/intl/en_us/mapfiles/marker_green';

  var myOptions = {
    zoom: 10,
    center: new google.maps.LatLng(0, 0),
    mapTypeControl: false,
    panControl: false,
    zoomControl: false,
    streetViewControl: false
  };

  var map = new google.maps.Map(document.getElementById('map'), myOptions);
  var places = new google.maps.places.PlacesService(map);

  var markers;

  function changeRegion(region) {
    var bounds = new google.maps.LatLngBounds(
      new google.maps.LatLng(region.sw.lat, region.sw.lng),
      new google.maps.LatLng(region.ne.lat, region.ne.lng));
    map.panToBounds(bounds);
    searchHotels();
  }

  var searchHotels = (function () {

    var markers = [];

    function clearMarkers() {
      markers.forEach(function (marker) {
        marker.setMap(null);
      });
      markers = [];
    }

    function handleResults(results) {
      results.forEach(function (result, i) {
        var markerLetter = String.fromCharCode('A'.charCodeAt(0) + i);
        var markerIcon = MARKER_PATH + markerLetter + '.png';

        var marker = new google.maps.Marker({
          position: result.geometry.location,
          animation: google.maps.Animation.DROP,
          icon: markerIcon
        });

        google.maps.event.addListener(marker, 'click', function () {
          selectHotel(result);
        });

        marker.setMap(map);

        markers.push(marker);
      });
    }

    return function searchHotels() {
      var search = {
        bounds: map.getBounds(),
        types: ['lodging']
      };

      places.nearbySearch(search, function(results, status) {
        if (status == google.maps.places.PlacesServiceStatus.OK) {
          clearMarkers();
          handleResults(results);
        }
      });
    };

  }());

  function selectHotel(place) {
    var search = { placeId: place.place_id };
    places.getDetails(search, function(place, status) {
      if (status != google.maps.places.PlacesServiceStatus.OK) {
        return;
      }
      publishPlace(place);
    });
  }

  var getAddress = (function () {

    function getLongName(comps, type) {
      for (var i = 0; i < comps.length; i += 1) {
        var comp = comps[i];
        if (comp.types.indexOf(type) >= 0) {
          return comp.long_name;
        }
      }
    }

    return function getAddress(place) {
      var comps = place.address_components;
      return {
        formatted: place.formatted_address,
        city: getLongName(comps, 'locality'),
        country: getLongName(comps, 'country'),
        street: getLongName(comps, 'route'),
        number: getLongName(comps, 'street_number'),
        postal_code: getLongName(comps, 'postal_code')
      };
    };

  }());

  function getHotel(place) {
    return {
      address: getAddress(place),
      name: place.name
    };
  }

  function publishPlace(place) {
    hub.publish('urn:example:type:Hotel', getHotel(place));
    hub.publish('urn:example:type:Address', getAddress(place));
  }

  hub.subscribe('urn:example:type:Region', function (topic, data) {
    changeRegion(data);
  });

  navigator.geolocation.getCurrentPosition(function (position) {
    var coords = position.coords;
    map.panTo({
      lat: coords.latitude,
      lng: coords.longitude
    });
  });

}());
