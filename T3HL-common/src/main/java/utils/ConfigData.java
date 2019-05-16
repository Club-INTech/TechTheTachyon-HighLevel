/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.
 *
 * INTech's HighLevel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * INTech's HighLevel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with it.  If not, see <http://www.gnu.org/licenses/>.
 **/

package utils;

import pfg.config.ConfigInfo;

/**
 * Enumération contenant la liste des valeurs configurable via un fichier (config/config.txt),
 * La valeur associée dans cette classe est celle attribuée par défaut, lorsque l'on fait une faute d'orthographe
 * dans le nom de la clé par exemple.
 *
 * @author rem
 */
public enum ConfigData implements ConfigInfo
{
    /**
     * Constantes (rarement modifiées)
     */
    TABLE_X(3000),
    TABLE_Y(2000),
    TEMPS_MATCH(100),
    ETHERNET_DEFAULT_TIME(1),

    /**
     * Couleur
     */
    COULEUR("jaune"),

    /**
     * Informations relatives au status du robot (Maître ou esclave ?)
     */
    MASTER(true),

    /**
     * Simulation active
     */
    SIMULATION(false),

    /**
     * Visualisation des actions active
     */
    VISUALISATION(true),

    /**
     * Si on utilise le Lidar
     */
    USING_LIDAR(false),

    /**
     * Si on utilise l'électron
     */
    USING_ELECTRON(false),

    /**
     * Si on utilise la balise pour le traitement d'images
     */
    USING_BALISE_IMAGE(false),

    /**
     * Si on utilise le panneau
     */
    USING_PANEL(true),


    /**
     * Si on se connecte au copain
     */
    CONNECT_TO_BUDDY(true),

    /**
     * Ips et ports des raspis, lidar & teensy
     */
    MASTER_IP("192.168.12.2"),
    MASTER_PORT(14500),
    TEENSY_MASTER_IP("192.168.1.1"),
    TEENSY_MASTER_PORT(13500),
    TEENSY_SLAVE_IP("192.168.12.3"),
    TEENSY_SLAVE_PORT(13500),
    LIDAR_DATA_PORT(17865),
    BALISE_IP("127.0.0.1"),
    BALISE_PORT(1111),
    IA_PORT(16000),
    ELECTRON_IP("192.168.12.69"),
    ELECTRON_PORT(18900),

    LOCALHOST("localhost"),
    LOCALSERVER_PORT(13550),
    LL_MASTER_SIMULATEUR(10001),
    HL_SLAVE_SIMULATEUR(20001),
    HL_MASTER_SIMULATEUR(20002),
    DEBUG_SIMULATEUR_PORT(19999),

    LED_COUNT(16),
    LED_PROGRAM_PORT(19500),

    /**
     * Timings
     */
    PING_INTERVAL(100), // durée entre deux pings, en ms (permet de confirmer que la connexion fonctionne encore)
    PING_TIMEOUT(500), // durée d'attente pour déclarer un timeout de la connexion, en ms (permet de confirmer que la connexion fonctionne encore)

    LOCOMOTION_OBSTRUCTED_TIMEOUT(2000),

    SCORE_UPDATE_PERIOD(100), // période entre deux mises à jour de l'affichage du score

    /**
     * Dimensions du robot
     */
    ROBOT_RAY(190),
    BUDDY_RAY(150),
    ENNEMY_RAY(220),

    /**
     * Threshold de comparaison de deux positions
     */
    VECTOR_COMPARISON_THRESHOLD(1000),

    /**
     * Paramètres du Graphe
     */
    NBR_NOEUDS_X(/*30*/45),
    NBR_NOEUDS_Y(/*20*/30),
    NBR_NOEUDS_CIRCLE(12),
    ESPACEMENT_CIRCLE(1.2),
    PARTITION_WIDTH(25),
    PARTITION_HEIGHT(25),

    /**
     * Paramètre Locomotion
     */
    LOCOMOTION_LOOP_DELAY(20),
    LOCOMOTION_DISTANCE_CHECK(200),
    LOCOMOTION_RADIUS_CHECK(200),

    /**
     * Paramètres de chemins
     */
    LIDAR_PROCESS_PATH("../bin/LiDAR_UST_10LX")
    ;

    /**
     * Valeur par défaut de la config (en dure)
     */
    private Object defaultValue;

    /**
     * Constructor with some default value
     *
     * @param defaultValue  valeur par défaut du paramètre
     */
    ConfigData(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Just a getter
     */
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Permet de changer la valeur par défaut
     * @param defaultValue
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
