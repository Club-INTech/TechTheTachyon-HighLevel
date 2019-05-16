#! /bin/bash

a=$(ls -t ./logs | head -1 | cut -f1 -d$'\t')
echo "./logs/$a"
