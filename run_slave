#!/bin/bash
sudo killall java
sudo fuser -k 19500
sudo killall LiDAR_UST_10LX
./reset_internet
cd bin/
sudo route add -net 192.168.0.0 netmask 255.255.255.0 eth0
java -jar TTHL-slave-1.0.jar
