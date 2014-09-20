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
| [testing](testing)                         | Test specific logic used by various modules. NOT PART OF THE RESULTING LIBRARY. |
| [utils](utils)                             | Utility library used by various modules. |


Directories next to Maven Modules:

| Directory                          | Description |
|------------------------------------|-------------|
| [composer-widget](composer-widget) | Cobalt Composer Widget |
| [demo-widgets](demo-widgets)       | Various demo widgets to demonstrate planner. |
| [scripts](scripts)                 | Shell scripts to start Planner Service. |


## Building

Clone the repository and change the current working directory.
Build the library by running `mvn install` (disable tests and Javadoc creation to speed up the build process).
Start the planner service via `./scripts/run-service.sh` (may require the granting of execution rights via `chmod +x` when not already done).
```
git clone {repo} cobalt
cd cobalt
mvn [-Dmaven.test.skip=true] [-Dmaven.javadoc.skip=true] install
chmod +x ./scripts/run-service.sh
./scripts/run-service.sh
```
The script `./scripts/run-service.sh` simplifies the specification of configuration parameters (cf. `./scripts/run-service.sh --help`).
These parameters can also be specified when using `mvn` (using `-Dkey="value"`), which is exactly what `./scripts/run-service.sh` does.
Possible configuration parameters are:

| Script Parameter       | Maven Parameter                         | Default Value       | Description |
|------------------------|-----------------------------------------|---------------------|-------------|
| `-d`<br>`--dataset`    | `vsr.cobalt.service.datasetDir`         | `--mem--`           | The directory to store the underlying database ([Jena](http://jena.apache.org/) [TDB](http://jena.apache.org/documentation/tdb/index.html)). Use `--mem--` for an in-memory dataset. |
| `-j`<br>`--java-opts`  | _n/a_                                   | _empty_             | Configure the JVM (equivalent to `export MAVEN_OPTS="..."`). |
| `-p`<br>`--port`       | `jetty.port`                            | `9000`              | Listen on the given port (Jetty configuration). |
| `-s`<br>`--seed`       | `vsr.cobalt.service.seedDataset="true"` | effectively `false` | Seed the dataset when given (required when using a new dataset). |
| `-w`<br>`--widgets`    | `vsr.cobalt.service.widgetDir`          | `$PWD`              | The directory to search for files containing semantic widget descriptors. |


## License

Licensed under the BSD 3-Clause License.
