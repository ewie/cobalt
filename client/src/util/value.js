/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


function get(self, name) {
  return self._fields[name];
}



function set(self, name, val) {
  self._fields[name] = val;
}



function equals(x, y) {
  return x === y ||
         x && typeof x.equals === 'function' && x.equals(y);
}



function toArray(values) {
  return values.map(function (v) { return v.toObject() });
}



function define(properties, proto) {

  function intern(name, props) {
    var p = properties[name];
    var v = props[name];
    if (p) {
      if (typeof p.intern === 'function') {
        return p.intern(v);
      } else if (p.type) {
        if (p.array) {
          return v ?
            v.map(function (w) { return new p.type(w) }) :
            [];
        }
        return v && new p.type(v);
      }
    } else {
      return v || p;
    }
  }


  function extern(self, name) {
    var p = properties[name];
    var v = get(self, name);
    if (p) {
      if (typeof p.extern === 'function') {
        return p.extern(v);
      } else if (p.type) {
        if (p.array) {
          return v.map(function (w) { return w.toObject() });
        }
        return v && v.toObject();
      } else {
        return v;
      }
    } else {
      return v;
    }
  }


  var names = Object.getOwnPropertyNames(properties);


  function Value(props) {
    if (props instanceof Value) {
      return props;
    }
    this._fields = {};
    for (var i = 0; i < names.length; i += 1) {
      var name = names[i];
      var value = intern(name, props);
      set(this, name, value);
    }
  }


  Value.fromArray = function (ary) {
    return (ary || []).map(function (v) { return new Value(v) });
  };


  Value.toArray = toArray;


  Value.prototype.equals = function (other) {
    if (!(other instanceof Value)) {
      return false;
    }
    var self = this;
    return names.every(function (name) {
      return equals(get(self, name), get(other, name));
    });
  };


  Value.prototype.toObject = function () {
    var self = this;
    return names.reduce(function (obj, name) {
      obj[name] = extern(self, name);
      return obj;
    }, {});
  };


  names.forEach(function (name) {
    if (Object.prototype.hasOwnProperty.call(Value.prototype, name)) {
      throw new Error("property name " + name + " cannot be used for value class");
    }
    Object.defineProperty(Value.prototype, name, {
      get: function () { return get(this, name) }
    });
  });


  if (proto) {
    Object.getOwnPropertyNames(proto).forEach(function (name) {
      if (Object.prototype.hasOwnProperty.call(Value.prototype, name)) {
        throw new Error("property " + name + " already defined");
      }
      Object.defineProperty(Value.prototype, name, Object.getOwnPropertyDescriptor(proto, name));
    });
  }


  return Value;

}



module.exports = {

  define: define,

  equals: equals

};

