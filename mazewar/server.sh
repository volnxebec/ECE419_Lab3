#!/bin/bash

ECE419_HOME=/cad2/ece419s/
JAVA_HOME=/usr/

if [ "$#" -eq 1 ] || [ "$#" -eq 2 ]; then
  echo "good"
else 
    echo "Usage: ./server.sh  <port> <number of players (optional)>"
    exit 1
fi

${JAVA_HOME}/bin/java Server $1 $2 

