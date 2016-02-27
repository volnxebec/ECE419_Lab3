#!/bin/bash

JAVA_HOME=/usr/

if [ "$#" -ne 4 ]; then
    echo "Usage: ./run.sh <server host> <server port> <client addr> <client port>"
    exit 1
fi

${JAVA_HOME}/bin/java Mazewar $1 $2 $3 $4

