#!/bin/bash


export JDK_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
export LD_LIBRARY_PATH=$JDK_HOME/jre/lib/amd64/server/:/opt/anaconda/lib/:$LD_LIBRARY_PATH

$PYTHON setup.py sdist bdist_wheel

mkdir -p $PREFIX/jpy_wheel

echo $PY_VER > /tmp/p_ver.txt

VER=$( echo $PY_VER | tr -d '.' )

cp -v $( find $SRC_DIR -name "jpy*$VER*$VER*.whl" ) $PREFIX/jpy_wheel 
