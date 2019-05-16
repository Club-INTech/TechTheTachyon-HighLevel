#! /bin/bash

a=$(ls -t ./logs | head -1 | cut -f1 -d$'\t')
cat "./logs/$a"
echo "# File opened: $a"
