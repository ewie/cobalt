# Cobalt Composer Widget

The Cobalt Composer Widget is a W3C Widget to consume the Cobalt planning service and configure a mashup within Apache Rave according to some user-selected plan.


## Dependencies

- [nodejs](http://nodejs.org) (0.10 or higher)


## Building

Clone the repository.
After changing the current working directory to the cloned repository, install dependencies using `npm`.
To build the widget run either `grunt` with either task `widget:debug` or `widget:release` the first will include sourcemaps, the latter will minify all JavaScript.
The resulting widgets are to be found in the projects root directory `cobalt-composer-widget.debug.wgt` or `cobalt-composer-widget.release.wgt`, depending on which Grunt task was used.
Move any of the resulting widgets to Apache Wookie's deploy directory `{deploy-dir}` of your running Apache Rave instance.
```
git clone {repo} cobalt
cd cobalt/composer-widget
npm i
grunt widget:{debug|release}
mv cobalt-composer-widget.{debug|relase}.wgt {deploy-dir}
```


## License

Licensed under the BSD 3-Clause License.
