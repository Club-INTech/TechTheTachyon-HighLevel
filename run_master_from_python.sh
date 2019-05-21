#!/bin/bash
sudo killall java
#sudo killall -10 python3
sudo fuser -k 19500
cd bin/
java -jar TTHL-master-1.0.jar