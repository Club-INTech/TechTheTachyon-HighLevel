Réseaux utilisés et IPs
=======================

Réseaux sans fil à configurer
=============================
wlan0
----
* RPi en Access Point

Adresses et interfaces
======================

Teensy du principal
-------------------
**Interfaces**

| Physique              | Nom de l'interface | Utilisations | Adresse statique  |
| --------------------- |:------------------:|:------------:|:-----------------:|
| Série                 | N/A                | HL           | N/A               |

**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|

Raspberry du principal
----------------------

**Interfaces**

| Physique              | Nom de l'interface | Utilisations                         | Adresse statique  |
| --------------------- |:------------------:|:------------------------------------:|:-----------------:|
| Port Ethernet (RJ45)  | eth0               | Lidar                                | 192.168.0.20/24   |
| WiFi intégré          | wlan0              | Connexion à l'AP de la Balise              | 192.168.12.2/24   |


**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|
| 127.0.0.1             | loopback           | 17865                    | Processus Lidar           |
| 127.0.0.1 (Serveur)   | wlan0              | ???                      | SSH                       |
| 127.0.0.1 (Serveur)   | wlan0              | 14500                    | Connexion au Slave        |
| 127.0.0.1 (Serveur)   | wlan0              | 1111                     | Balise                    |
| 192.168.12.69/24      | wlan0              | 18900                    | Comm HL<->Electron        |
| 192.168.0.10/24       | eth0               | ? (voir datasheet lidar) | Lidar                     |

Secondaire
==========
Teensy du secondaire
-------------------
**Interfaces**

| Physique              | Nom de l'interface | Utilisations | Adresse statique  |
| --------------------- |:------------------:|:------------:|:-----------------:|
| Série                 | N/A                | HL           | N/A               |

**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|

Raspberry du secondaire
----------------------

**Interfaces**

| Physique              | Nom de l'interface | Utilisations             | Adresse statique  |
| --------------------- |:------------------:|:------------------------:|:-----------------:|
| Port Ethernet (RJ45)  | eth0               | Lidar                    | 192.168.0.20/24   |
| WiFi intégré          | wlan0              | Connexion à l'AP de la balise | 192.168.12.3/24   |


**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation               |
| --------------------- |:------------------:|:------------------------:|:-------------------------:|
| 127.0.0.1             | loopback           | 17865                    | Processus Lidar           |
| 192.168.12.2/24       | wlan0              | 14500                    | Connexion au Master       |
| 192.168.0.10/24       | eth0               | ? (voir datasheet lidar) | Lidar                     |


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

Nuc
======

**Interfaces**

| Physique              | Nom de l'interface | Utilisations      | Adresse statique  |
| --------------------- |:------------------:|:-----------------:|:-----------------:|
| WiFi intégré          | wlan0              | k6op détection de palet| 192.168.12.12              |


Electron
========
**Interfaces**

| Physique              | Nom de l'interface | Utilisations            | Adresse statique  |
| --------------------- |:------------------:|:-----------------------:|:-----------------:|
| WiFi intégré          | N/A                | AP | 192.168.42.69/24  |


**Adresses**

| Adresse destinataire  | Interface          | Port                     | Utilisation                   |
| --------------------- |:------------------:|:------------------------:|:-----------------------------:|
| 127.0.0.1 (Serveur)   | N/A (WiFi intrégé) | 18900                    | Comm avec le HL du principal  |
