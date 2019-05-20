#! /usr/bin/python3.5m
import sys
import re

if len(sys.argv) < 2:
    print("Nombre nom valide d'arguments: ", sys.argv[0], "fichier", "[CHANNEL]")
    exit(1)

pattern = re.compile("\[.*(?P<time>[0-9][0-9]:[0-9][0-9]:[0-9][0-9].[0-9][0-9][0-9]) (\(Time: (?P<matchtime>[0-9][0-9][0-9].[0-9][0-9][0-9]))?.* (?P<channel>[A-Z]+) \(.*\] (?P<message>.*)$")
path = sys.argv[1]

channelToShow = "ORDERS"
if len(sys.argv) >= 3:
    channelToShow = sys.argv[2]

for l in open(path):
    line = l.rstrip('\n')
    match = pattern.match(line)
    if match is None:
        continue
    time = match.group("time")
    matchTime = match.group("matchtime")
    if matchTime is None:
        matchTime = "Démarrage"

    try:
        channel = match.group("channel")
        message = match.group("message")

        if channel == channelToShow:
            print(matchTime+"\t"+message)
    except Exception as e:
        print(e)
        continue
