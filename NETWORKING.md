Réseaux utilisés et IPs
=======================

Réseaux sans fil à configurer
=============================
wlan0
----
* Secondaire->Principal: Raspi du Principal en Access Point (wlan0)
* SSH->Principal: Raspi du Principal en Access Point (wlan0)
* Balise->Principal: Raspi du Principal en Access Point (wlan0)

wlan1
----
* Principal->Electron: Electron en Access Point, configurer une interface wlan sur le principal (wlan1)

wlan2
----
* Principal-Internet: Configurer wlan2 pour se connecter sur Internet, si besoin est

Adresses et interfaces
======================

Teensy du principal
-------------------
**Interfaces**

| Physique              | Nom de l'interface | Utilisations | Adresse statique  |
| --------------------- |:------------------:|:------------:|:-----------------:|
| Wiz (RJ45)            | N/A                | HL           | 192.168.1.1/24    |

**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|
| 192.168.1.2/24        | N/A (Wiz)          | 13500                    | Comm HL<->LL              |

Raspberry du principal
----------------------

**Interfaces**

| Physique              | Nom de l'interface | Utilisations                         | Adresse statique  |
| --------------------- |:------------------:|:------------------------------------:|:-----------------:|
| Port Ethernet (RJ45)  | eth0               | Teensy                               | 192.168.1.2/24    |
| Adaptateur RJ/USB     | eth1               | Lidar                                | 192.168.0.20/24  |
| WiFi intégré          | wlan0              | AP: SSH, Slave, Balise               | ???               |
| Dongle WiFi USB n°1   | wlan1              | Connexion au réseau de l'électron    |                   |
| Dongle WiFi USB n°2 ? | wlan2              | Connexion à Internet? (local INTech) |                   |


**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|
| 127.0.0.1             | loopback           | 17865                    | Processus Lidar           |
| 127.0.0.1 (Serveur)   | wlan0              | ???                      | SSH                       |
| 127.0.0.1 (Serveur)   | wlan0              | 14500                    | Connexion au Slave        |
| 127.0.0.1 (Serveur)   | wlan0              | 1111                     | Balise                    |
| 192.168.?.69/24       | wlan1?             | 18900                    | Comm HL<->Electron        |
| 192.168.1.1/24        | eth0               | 13500                    | Comm HL<->LL              |
| 192.168.0.10/24       | eth1               | ? (voir datasheet lidar) | Lidar                     |

Secondaire
==========
Teensy du secondaire
-------------------
**Interfaces**

| Physique              | Nom de l'interface | Utilisations | Adresse statique  |
| --------------------- |:------------------:|:------------:|:-----------------:|
| Wiz (RJ45)            | N/A                | HL           | 192.168.1.1/24    |

**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|
| 192.168.1.2/24        | ??? (Wiz)          | 13500                    | Comm HL<->LL              |

Raspberry du secondaire
----------------------

**Interfaces**

| Physique              | Nom de l'interface | Utilisations | Adresse statique  |
| --------------------- |:------------------:|:------------:|:-----------------:|
| Port Ethernet (RJ45)  | eth0               | Teensy       | 192.168.1.2/24    |
| Adaptateur RJ/USB     | eth1               | Lidar        | 192.168.0.20/24   |
| WiFi intégré          | wlan0              | Comm Master  | 192.168.?.?/24    |


**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|
| 127.0.0.1             | loopback           | 17865                    | Processus Lidar           |
| 192.168.0.10/24       | eth1               | ? (voir datasheet lidar) | Lidar                     |
| 192.168.1.1/24        | eth0               | 13500                    | Comm HL<->LL              |
| 192.168.?.1/24        | wlan0              | 14500                    | Connexion au Master       |


Balise
======
***TODO***

**Interfaces**

| Physique              | Nom de l'interface | Utilisations | Adresse statique  |
| --------------------- |:------------------:|:------------:|:-----------------:|
| TODO                  | TODO               | TODO         | TODO              |


**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|
| TODO                  | TODO               | TODO                     | TODO                      |

Electron
========
**Interfaces**

| Physique              | Nom de l'interface | Utilisations | Adresse statique  |
| --------------------- |:------------------:|:------------:|:-----------------:|
| WiFi intégré          | N/A                | Comm avec HL | 192.168.?.69/24   |


**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation                   |
| --------------------- |:------------------:|:------------------------:|:-----------------------------:|
| 127.0.0.1 (Serveur)   | N/A (WiFi intrégé) | 18900                    | Comm avec le HL du principal  |
