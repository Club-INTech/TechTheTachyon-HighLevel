#! /bin/bash

a=$(ls -t ./logs/*\ -\ everything.log | head -1 | cut -f1 -d$'\t')
echo "$a"
