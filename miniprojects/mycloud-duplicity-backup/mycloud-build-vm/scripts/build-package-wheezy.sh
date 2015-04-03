#!/bin/sh
if [ $# -ne 1 ]; then echo "One argument - package name - required"; exit -1; fi
PACKAGE=$1
cd /wdmc-build/64k-wheezy
./build.sh $PACKAGE

echo "The $PACKAGE is hopefully in /wdmc-build/<scenario>/build/root"
