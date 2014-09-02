/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


(function () {

  var map = L.map('map').setView([0, 0], 1);

  var markerProps = {
    draggable: true,
    icon: L.divIcon({ className: 'icon', iconSize: null })
  };

  var isOverRegion;

  var centerMarker = L.marker([0, 0], markerProps)
    .on('dragstart', fixRegion)
    .on('drag', moveRegion);

  var perimMarker = L.marker([0, 0.1], markerProps)
    .on('drag', updateRegion)
    .on('click', function () { return false });

  var regionMarker = L.circle([0, 0], 100, { color: 'red' })
    .on('mouseover', function () { isOverRegion = true })
    .on('mouseout', function () { isOverRegion = false })
    .on('dblclick', function (ev) {
      console.log('confirm region');

      var bounds = regionMarker.getBounds();

      hub.publish('urn:example:type:Region', {
        sw: {
          lat: bounds.getSouthWest().lat,
          lng: bounds.getSouthWest().lng
        },
        ne: {
          lat: bounds.getNorthEast().lat,
          lng: bounds.getNorthEast().lng
        }
      });

      return false;
    });

  map.addLayer(centerMarker);
  map.addLayer(perimMarker);
  map.addLayer(regionMarker);

  var v;

  function fixRegion() {
    var center = centerMarker.getLatLng();
    var perim = perimMarker.getLatLng();

    v = [
      perim.lat - center.lat,
      perim.lng - center.lng
    ];
  }

  function moveRegion() {
    var center = centerMarker.getLatLng();
    perimMarker.setLatLng([ center.lat + v[0], center.lng + v[1] ]);
    updateRegion();
  }

  function setRegion(coords) {
    fixRegion();
    centerMarker.setLatLng(coords);
    moveRegion();
  }

  function updateRegion() {
    var center = centerMarker.getLatLng();
    var perim = perimMarker.getLatLng();

    regionMarker
      .setLatLng(center)
      .setRadius(center.distanceTo(perim));
  }

  updateRegion();

  navigator.geolocation.getCurrentPosition(function (position) {
    var coords = L.latLng(position.coords.latitude, position.coords.longitude);
    setRegion(coords);
    map.fitBounds(regionMarker.getBounds());
  });

  L.tileLayer('http://b.tile.osm.org/{z}/{x}/{y}.png', {
      attribution: [
        'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors',
        '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>'
      ].join(', '),
      maxZoom: 18
  }).addTo(map);

  map.on('click', function (ev) {
    // ignore middle button
    if (!isOverRegion && ev.originalEvent.button === 0) {
      setRegion(ev.latlng);
    }
  });

}());
