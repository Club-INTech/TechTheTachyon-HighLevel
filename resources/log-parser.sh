#!/usr/bin/env bash
if [ $# -lt 3 ]; then
    echo "Usage : log-parser.sh <input file> <output file> [channel names]"
    exit
fi
FILE=$1
OUT=$2
shift
shift
cat $FILE | grep $@ > $OUT