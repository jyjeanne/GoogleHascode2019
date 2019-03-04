#!/usr/bin/env bash

# zip source folder
gradle -q srcZip

# run treatment on specific file
java -jar "./build/libs/GoogleHascode2019.jar" $1
