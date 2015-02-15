# Demo Widgets

A set of demo widgets to be used as demonstration of the Cobalt Mashup Planner (parent directory).


## Dependencies

Required command line tools (these are standard Unix tools):

- `grep`
- `sed`
- `zip`


## Building

Clone the repository to a desired location, e.g. `cobalt`.
After changing the current working directory to the cloned repository demo widgets `cobalt/demo-widgets`, run `make widgets`.
Move the resulting widgets `*.wgt` to Apache Wookie's deploy directory `{deploy-dir}` of your running Apache Rave instance.
```
git clone {repo} cobalt
cd cobalt/demo-widgets
make widgets
mv *.wgt {deploy-dir}
```
The files to bundle in Zip-Archives are determined by examining the files declared in each widgets `config.xml`.
Care has to be taken when authoring new widgets that should be handled by this Makefile.
Make sure each `<content/>` element declares the respective file (attribute `src`) as their very first attribute (cf. example below).
This is requires as the `config.xml` is processed as plain text (using `sed` and `grep`) rather than as proper XML (e.g. traversing its DOM).
```xml
<?xml version="1.0" encoding="utf-8"?>
<widget xmlns="http://www.w3.org/ns/widgets">
 <!-- Widget Meta Data -->
 <content src="index.html" type="text/html"/>
 <content src="style.css" type="text/css"/>
 <content src="main.js" type="application/javascript"/>
 <!-- Features, etc. -->
</widget>
```


## License

All widgets are licensed under the BSD 3-Clause License.
