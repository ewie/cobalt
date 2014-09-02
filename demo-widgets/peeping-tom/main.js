/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


(function () {

  function get(url, callback) {
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status >= 200 && xhr.status < 300) {
          callback(JSON.parse(xhr.responseText));
        }
      }
    };

    xhr.open('GET', url);

    xhr.send();
  }

  hub.subscribe('urn:example:type:Address', function (topic, data) {
    updateImages(data.formatted);
  });

  // http://stackoverflow.com/a/15417769
  var URL = 'http://api.bing.net/json.aspx?Appid=49EB4B94127F7C7836C96DEB3F2CD8A6D12BDB71&sources=image&query=';

  function updateImages(address) {
    var images = document.getElementById('images');

    var url = widget.proxify(encodeURIComponent(URL + encodeURIComponent(address)));

    images.textContent = '';

    get(url, function (data) {
      console.log(data);
      data.SearchResponse.Image.Results.forEach(function (r) {
        var img = document.createElement('img');
        img.setAttribute('src', r.Thumbnail.Url);
        images.appendChild(img);
      });
    });
  }

}());
