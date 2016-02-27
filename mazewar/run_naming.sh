#!/bin/bash

JAVA_HOME=/usr/

if [ "$#" -ne 5 ]; then
    echo "Usage: ./run.sh <server host> <server port> <clientAddr> <first> <totalPlayer>"
    exit 1
fi

${JAVA_HOME}/bin/java Mazewar $1 $2 $3 $4 $5

