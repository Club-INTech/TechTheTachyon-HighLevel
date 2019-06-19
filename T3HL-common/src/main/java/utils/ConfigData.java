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

import pfg.config.BaseConfigInfo;
import pfg.config.ConfigInfo;
import pfg.config.DerivedConfigInfo;

/**
 * Enumération contenant la liste des valeurs configurable via un fichier (config/config.txt),
 * La valeur associée dans cette classe est celle attribuée par défaut, lorsque l'on fait une faute d'orthographe
 * dans le nom de la clé par exemple.
 *
 * @author rem, jglrxavpok
 */
public interface ConfigData
{
    /**
     * Constantes (rarement modifiées)
     */
    ConfigInfo<Integer> TABLE_X = new BaseConfigInfo<>(3000, Integer.TYPE);
    ConfigInfo<Integer> TABLE_Y = new BaseConfigInfo<>(2000, Integer.TYPE);
    ConfigInfo<Long> TEMPS_MATCH = new BaseConfigInfo<>(100L, Long.TYPE);
    ConfigInfo<Long> ETHERNET_DEFAULT_TIME = new BaseConfigInfo<>(1L, Long.TYPE);

    /**
     * Le script d'homologation doit-il être exécuté
     */
    ConfigInfo<Boolean> HOMOLOGATION = new BaseConfigInfo<>(false, Boolean.TYPE);

    /**
     * Couleur
     */
    ConfigInfo<String> COULEUR = new BaseConfigInfo<>("jaune", String.class);
    ConfigInfo<Boolean> SYMETRY = new DerivedConfigInfo<>(false, Boolean.TYPE, config -> config.get(COULEUR).equals("violet"));

    /**
     * Informations relatives au status du robot (Maître ou esclave ?)
     */
    ConfigInfo<Boolean> MASTER = new BaseConfigInfo<>(true, Boolean.TYPE);

    /**
     * Simulation active
     */
    ConfigInfo<Boolean> SIMULATION = new BaseConfigInfo<>(false, Boolean.TYPE);

    /**
     * Visualisation des actions active
     */
    ConfigInfo<Boolean> VISUALISATION = new BaseConfigInfo<>(true, Boolean.TYPE);

    /**
     * Si on utilise le Lidar
     */
    ConfigInfo<Boolean> USING_LIDAR = new BaseConfigInfo<>(false, Boolean.TYPE);

    /**
     * Si on utilise l'électron
     */
    ConfigInfo<Boolean> USING_ELECTRON = new BaseConfigInfo<>(false, Boolean.TYPE);

    /**
     * Si on utilise la balise pour le traitement d'images
     */
    ConfigInfo<Boolean> USING_BALISE_IMAGE = new BaseConfigInfo<>(true, Boolean.TYPE);

    /**
     * Si on utilise le panneau
     */
    ConfigInfo<Boolean> USING_PANEL = new BaseConfigInfo<>(true, Boolean.TYPE);


    /**
     * Si on se connecte au copain
     */
    ConfigInfo<Boolean> CONNECT_TO_BUDDY = new BaseConfigInfo<>(true, Boolean.TYPE);

    /**
     * Ips et ports des raspis, lidar & teensy
     */
    ConfigInfo<String> MASTER_IP = new BaseConfigInfo<>("192.168.12.2", String.class);
    ConfigInfo<Integer> MASTER_PORT = new BaseConfigInfo<>(14500, Integer.TYPE);
    ConfigInfo<String> TEENSY_MASTER_IP = new BaseConfigInfo<>("192.168.1.1", String.class);
    ConfigInfo<Integer> TEENSY_MASTER_PORT = new BaseConfigInfo<>(13500, Integer.TYPE);
    ConfigInfo<String> TEENSY_SLAVE_IP = new BaseConfigInfo<>("192.168.12.3", String.class);
    ConfigInfo<Integer> TEENSY_SLAVE_PORT = new BaseConfigInfo<>(13500, Integer.TYPE);
    ConfigInfo<Integer> LIDAR_DATA_PORT = new BaseConfigInfo<>(17865, Integer.TYPE);
    ConfigInfo<String> BALISE_IP = new BaseConfigInfo<>("192.168.12.12", String.class);
    ConfigInfo<Integer> BALISE_PORT = new BaseConfigInfo<>(42111, Integer.TYPE);
    ConfigInfo<Integer> IA_PORT = new BaseConfigInfo<>(16000, Integer.TYPE);
    ConfigInfo<String> ELECTRON_IP = new BaseConfigInfo<>("192.168.42.69", String.class);
    ConfigInfo<Integer> ELECTRON_PORT = new BaseConfigInfo<>(18900, Integer.TYPE);

    ConfigInfo<String> LOCALHOST = new BaseConfigInfo<>("localhost", String.class);
    ConfigInfo<Integer> LOCALSERVER_PORT = new BaseConfigInfo<>(13550, Integer.TYPE);
    ConfigInfo<Integer> LL_MASTER_SIMULATEUR = new BaseConfigInfo<>(10001, Integer.TYPE);
    ConfigInfo<Integer> HL_SLAVE_SIMULATEUR = new BaseConfigInfo<>(20001, Integer.TYPE);
    ConfigInfo<Integer> HL_MASTER_SIMULATEUR = new BaseConfigInfo<>(20002, Integer.TYPE);
    ConfigInfo<Integer> DEBUG_SIMULATEUR_PORT = new BaseConfigInfo<>(19999, Integer.TYPE);

    ConfigInfo<Boolean> MODE_MONTHLERY = new BaseConfigInfo<>(false, Boolean.TYPE);
    ConfigInfo<Boolean> OPEN_THE_GATE = new BaseConfigInfo<>(false, Boolean.TYPE);
    ConfigInfo<Boolean> ZONE_CHAOS_TEST = new BaseConfigInfo<>(true, Boolean.TYPE);


    /**
     * Paramètres du panneau
     */
    ConfigInfo<Integer> LED_COUNT = new BaseConfigInfo<>(16, Integer.TYPE);
    ConfigInfo<Integer> LED_PROGRAM_PORT = new BaseConfigInfo<>(19500, Integer.TYPE);
    ConfigInfo<Boolean> USING_7_SEGMENTS = new BaseConfigInfo<>(true, Boolean.TYPE);

    /**
     * Timings
     */
    ConfigInfo<Long> PING_INTERVAL = new BaseConfigInfo<>(100L, Long.TYPE); // durée entre deux pings, en ms (permet de confirmer que la connexion fonctionne encore)
    ConfigInfo<Long> PING_TIMEOUT = new BaseConfigInfo<>(500L, Long.TYPE); // durée d'attente pour déclarer un timeout de la connexion, en ms (permet de confirmer que la connexion fonctionne encore)
    ConfigInfo<Long> BALANCE_WAIT_TIME = new BaseConfigInfo<>(14000L, Long.TYPE); // combien de temps on attend le secondaire
    ConfigInfo<Long> BALANCE_SLAVE_WAIT_TIME = new BaseConfigInfo<>(3000L, Long.TYPE);
    ConfigInfo<Long> TIME_BETWEEN_POS_UPDATES = new BaseConfigInfo<>(200L, Long.TYPE); // durée entre deux envois de positions entre principal et secondaire

    ConfigInfo<Long> LOCOMOTION_OBSTRUCTED_TIMEOUT = new BaseConfigInfo<>(2000L, Long.TYPE);

    ConfigInfo<Long> SCORE_UPDATE_PERIOD = new BaseConfigInfo<>(100L, Long.TYPE); // période entre deux mises à jour de l'affichage du score

    /**
     * Dimensions du robot
     */
    ConfigInfo<Integer> ROBOT_RAY = new BaseConfigInfo<>(190, Integer.TYPE);
    ConfigInfo<Integer> BUDDY_RAY = new BaseConfigInfo<>(150, Integer.TYPE);
    ConfigInfo<Integer> ENNEMY_RAY = new BaseConfigInfo<>(220, Integer.TYPE);

    /**
     * Threshold de comparaison de deux positions
     */
    ConfigInfo<Integer> VECTOR_COMPARISON_THRESHOLD = new BaseConfigInfo<>(5, Integer.TYPE);

    /**
     * Paramètres du Graphe
     */
    ConfigInfo<Integer> NBR_NOEUDS_X = new BaseConfigInfo<>(/*30*/45, Integer.TYPE);
    ConfigInfo<Integer> NBR_NOEUDS_Y = new BaseConfigInfo<>(/*20*/30, Integer.TYPE);
    ConfigInfo<Integer> NBR_NOEUDS_CIRCLE = new BaseConfigInfo<>(12, Integer.TYPE);
    ConfigInfo<Double> ESPACEMENT_CIRCLE = new BaseConfigInfo<>(1.2, Double.TYPE);
    ConfigInfo<Integer> PARTITION_WIDTH = new BaseConfigInfo<>(25, Integer.TYPE);
    ConfigInfo<Integer> PARTITION_HEIGHT = new BaseConfigInfo<>(25, Integer.TYPE);

    /**
     * Paramètre Locomotion
     */
    ConfigInfo<Long> LOCOMOTION_LOOP_DELAY = new BaseConfigInfo<>(20L, Long.TYPE);
    ConfigInfo<Integer> LOCOMOTION_DISTANCE_CHECK = new BaseConfigInfo<>(200, Integer.TYPE);
    ConfigInfo<Integer> LOCOMOTION_RADIUS_CHECK = new BaseConfigInfo<>(200, Integer.TYPE);

    /**
     * Paramètres de chemins
     */
    ConfigInfo<String> LIDAR_PROCESS_PATH = new BaseConfigInfo<>("../bin/LiDAR_UST_10LX", String.class);

    /**
     * On se recale à l'accélérateur? Si non c'est à x6
     */
    ConfigInfo<Boolean> RECALAGE_ACC = new BaseConfigInfo<>(false, Boolean.TYPE);

    ConfigInfo<Boolean> RECALAGE_MECA_ACC = new BaseConfigInfo<>(true, Boolean.TYPE);

    ConfigInfo<Boolean> RECALAGE_MECA_BLUE_ACC = new BaseConfigInfo<>(true, Boolean.TYPE);

    ConfigInfo<Boolean> SECOURS = new BaseConfigInfo<>(false, Boolean.TYPE);

    ConfigInfo<?>[] ALL_VALUES = ConfigInfo.findAllIn(ConfigData.class);

    static ConfigInfo<?>[] values() {
        return ALL_VALUES;
    }
}
