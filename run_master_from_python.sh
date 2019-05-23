#!/bin/bash
sudo killall java
sudo fuser -k 19500
cd bin/
sudo route delete default gw 0.0.0.0 eth0
sudo route add -net 192.168.0.0 netmask 255.255.255.0 eth0
java -jar TTHL-master-1.0.jar
