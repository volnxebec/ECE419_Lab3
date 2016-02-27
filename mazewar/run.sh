#!/bin/bash

JAVA_HOME=/usr/

if [ "$#" -ne 3 ]; then
    echo "Usage: ./run.sh <server host> <server port> <clientAddr>"
    exit 1
fi

${JAVA_HOME}/bin/java Mazewar $1 $2 $3

