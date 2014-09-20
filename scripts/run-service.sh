#!/usr/bin/env sh
#
# Copyright (c) 2014, Erik Wienhold
# All rights reserved.
#
# Licensed under the BSD 3-Clause License.

usage() {
	cat <<-EOF
	Cobalt user interface mashup planner
	Copyright (c) 2014 Erik Wienhold

	  -d --dataset [DIR]  dataset directory (default in-memory dataset)
	  -h --help           this help
	  -j --java-opts      optional java options
	  -p --port [PORT]    port number (default 9000)
	  -s --seed           seed the dataset with models found under widgets directory
	  -w --widgets [DIR]  directory to recursively search for widgets (default current directory)
	EOF
}

NAMESPACE=vsr.cobalt.service

DATASET=--mem--
PORT=9000
SEED=false
WIDGETS=.
JAVA_OPTS=""

while [ "$1" != "" ]; do
  case "$1" in
    -d|--dataset)
      DATASET=$2
      shift 2
      ;;
    -h|--help)
      usage
      exit
      ;;
    -j|--java-opts)
      JAVA_OPTS=$2
      shift 2
      ;;
    -p|--port)
      PORT=$2
      shift 2
      ;;
    -s|--seed)
      SEED=true
      shift
      ;;
    -w|--widgets)
      WIDGETS=$2
      shift 2
      ;;
    -*)
      echo "unknown option $1"
      exit 1
      ;;
  esac
done

export MAVEN_OPTS="$JAVA_OPTS"

mvn -pl service jetty:run \
  -Djetty.port=$PORT \
  -D$NAMESPACE.widgetDir="$WIDGETS" \
  -D$NAMESPACE.datasetDir="$DATASET" \
  -D$NAMESPACE.seedDataset="$SEED"
