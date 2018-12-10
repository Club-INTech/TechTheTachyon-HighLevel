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
     * Paramètres du log
     */
    PRINT_LOG(true),
    SAVE_LOG(true),

    /**
     * Couleur
     */
    COULEUR("violet"),

    /**
     * Informations relatives au status du robot (Maître ou esclave ?)
     */
    MASTER(true),

    /**
     * Ips et ports des raspis, lidar & teensy
     */
    MASTER_IP("192.168.0.3"),
    MASTER_PORT(14500),
    TEENSY_MASTER_IP("192.168.0.1"),
    TEENSY_MASTER_PORT(13500),
    TEENSY_SLAVE_IP("192.168.0.2"),
    TEENSY_SLAVE_PORT(13500),
    LIDAR_PORT(15500),

    LOCALHOST("localhost"),
    LOCALSERVER_PORT(13550),

    /**
     * Dimensions du robot
     */
    ROBOT_RAY(220),
    BUDDY_RAY(150),
    ENNEMY_RAY(220),

    /**
     * Threshold de comparaison de deux positions
     */
    VECTOR_COMPARISON_THRESHOLD(60),

    /**
     * Paramètres du Graphe
     */
    NBR_NOEUDS_X(30),
    NBR_NOEUDS_Y(20),
    NBR_NOEUDS_CIRCLE(12),
    ESPACEMENT_CIRCLE(1.2),

    /**
     * Paramètre Locomotion
     */
    LOCOMOTION_LOOP_DELAY(20),
    LOCOMOTION_DISTANCE_CHECK(200),
    LOCOMOTION_RADIUS_CHECK(40)
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

}
