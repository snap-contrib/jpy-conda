#!/bin/bash

export JDK_HOME=/usr/lib/jvm/java-1.8.0-openjdk.x86_64
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk.x86_64
export LD_LIBRARY_PATH=$JDK_HOME/jre/lib/amd64/server/:/opt/anaconda/lib/:$LD_LIBRARY_PATH

$PYTHON setup.py bdist_wheel

# Add more build steps here, if they are necessary.

# See
# http://docs.continuum.io/conda/build.html
# for a list of environment variables that are set during the build process.
