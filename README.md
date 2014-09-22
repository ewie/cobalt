# Cobalt Mashup Planner

The Cobalt Composer Widget is a W3C Widget to consume the Cobalt planning service and configure a mashup within Apache Rave according to some user-selected plan.


## Project Structure

The project uses various Maven Modules to organize code as logical units.
In addition, there are non-module directories containing non-Java code.

Maven Modules:

| Module                                     | Description |
|--------------------------------------------|-------------|
| [models](models)                           | The domain models used by the planner and repository. |
| [planner](planner)                         | The planner logic; i.e. planning graph, planning process, and plan search. |
| [repository-semantic](repository-semantic) | The default repository using based on semantic widget descriptions (cf. [Mashup Ontology](repository-semantic/src/main/resources/ont/mashup.ttl)). |
| [service](service)                         | The planner service expsoing and endpoint to consume the planner via HTTP. Uses the semantic repository. |
| [testing](testing)                         | Test specific logic used by various modules. _Not part of the resulting library._ |
| [utils](utils)                             | Utility library used by various modules. |


Directories next to Maven Modules:

| Directory                          | Description |
|------------------------------------|-------------|
| [composer-widget](composer-widget) | Cobalt Composer Widget |
| [demo-widgets](demo-widgets)       | Various demo widgets to demonstrate planner. |
| [scripts](scripts)                 | Shell scripts to start Planner Service. |


## Building

Clone the repository and change the current working directory.
Build the library by running `mvn install` (optionally disable tests and Javadoc creation to speed up the build process).
Start the planner service via `./scripts/run-service.sh` (may require the granting of execution rights via `chmod +x` when not already done).
```
git clone {repo} cobalt
cd cobalt
mvn [-Dmaven.test.skip=true] [-Dmaven.javadoc.skip=true] install
chmod +x ./scripts/run-service.sh
./scripts/run-service.sh
```
The script `./scripts/run-service.sh` invokes `mvn -pl service jetty:run {JVM Parameters}` and simplifies the specification of configuration parameters (cf. `./scripts/run-service.sh --help`).
These parameters can also be specified when using `mvn` (using `-Dkey="value"`), which is exactly what `./scripts/run-service.sh` does.

Alternatively, the service is also packaged as an executable WAR using an embedded Jetty server.
Invoke it using `java -jar` referencing the WAR file (substitute `{version}` with an actual version number).
The same configuration parameters as for the Maven-based invocation apply.
```
java -jar {JVM Parameters} service/target/cobalt-service-{version}.war
```

Possible configuration parameters are:

| Script Parameter       | JVM Parameter                           | Default Value       | Description |
|------------------------|-----------------------------------------|---------------------|-------------|
| `-d`<br>`--dataset`    | `vsr.cobalt.service.datasetDir`         | `--mem--`           | The directory to store the underlying database ([Jena](http://jena.apache.org/) [TDB](http://jena.apache.org/documentation/tdb/index.html)). Use `--mem--` for an in-memory dataset. |
| `-j`<br>`--java-opts`  | _n/a_                                   | _empty_             | Configure the JVM (equivalent to `java` console arguments or `export MAVEN_OPTS="..."` when using `mvn`). |
| `-p`<br>`--port`       | `jetty.port`                            | `9000`              | Listen on the given port (Jetty configuration). |
| `-s`<br>`--seed`       | `vsr.cobalt.service.seedDataset=true`   | effectively `false` | Seed the dataset when given (required when using a new dataset). |
| `-w`<br>`--widgets`    | `vsr.cobalt.service.widgetDir`          | `$PWD`              | The directory to search for files containing semantic widget descriptors. |


## Demo

To run a demo, build the project and start up your Apache Rave installation.
Run the planner service as described above using directory [demo-widgets](demo-widgets) to import widget descriptors from.
Build and deploy the [Composer Widget](composer-widget) and [demo widgets](demo-widgets) as described in the respective READMEs.
Navigate your browser to the location of your running Apache Rave instance and manually add the Composer Widget to an empty workspace.

Within the service dialogue, specify the goal mashup:
```
@prefix : <https://vsr.informatik.tu-chemnitz.de/projects/2014/cobalt/mashup#> .

[] a :Mashup ;
  :realizesFunctionality
    <urn:example:fn:HotelBooking> ,
    <urn:example:fn:ShowImages> .
```
Omit any of the two functionalities to get different mashups.
Provide a maximum plan depth, i.e. maximum number of execution steps (e.g. 4), to ignore any plans with _likely_ to many steps.
Let the action composition strategy compose _minimal_ precursors and disable the composition of functionality and property providers (uncheck respective checkboxes).
Specify the planning service endpoint, e.g. `http://localhost:9000` in the default case.
Finally submit the request, select any of the presented plans, and execute the resulting mashup as instructed by the execution dialogue.
_Play with different action composition strategies to see their effect on resulting plans._


## License

Licensed under the BSD 3-Clause License.
