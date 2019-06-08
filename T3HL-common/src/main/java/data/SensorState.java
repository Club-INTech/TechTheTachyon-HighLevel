/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.

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

package data;

import utils.Log;

/**
 * Liste les données du robot et leurs état
 *
 * @author william
 */
public class SensorState<DataType> {
    public static final SensorState<Boolean> MOVING = new SensorState<>("MOVING", false, Boolean.class);
    public static final SensorState<Boolean> STUCKED = new SensorState<>("STUCKED", false, Boolean.class);
    public static final SensorState<Boolean> LEFT_ELEVATOR_MOVING = new SensorState<>("LEFT_ELEVATOR_MOVING", false, Boolean.class);
    public static final SensorState<Boolean> RIGHT_ELEVATOR_MOVING = new SensorState<>("RIGHT_ELEVATOR_MOVING", false, Boolean.class);
    public static final SensorState<Boolean> ACTUATOR_ACTUATING = new SensorState<>("ACTUATOR_ACTUATING", false, Boolean.class);
    public static final SensorState<Long> LAST_PONG = new SensorState<>("LAST_PONG", -1L, Long.class);
    public static final SensorState<Boolean> WAITING_JUMPER = new SensorState<>("WAITING_JUMPER", false, Boolean.class);
    public static final SensorState<Boolean> ELECTRON_ACTIVATED = new SensorState<>("ELECTRON_ACTIVATED", false, Boolean.class);
    public static final SensorState<Boolean> ELECTRON_ARRIVED = new SensorState<>("ELECTRON_ARRIVED", false, Boolean.class);
    public static final SensorState<Boolean> LEFT_ARM_MOVING = new SensorState<>("LEFT_ARM_MOVING",false, Boolean.class);
    public static final SensorState<Boolean> RIGHT_ARM_MOVING = new SensorState<>("RIGHT_ARM_MOVING",false, Boolean.class);
    public static final SensorState<Boolean> RECALAGE_LIDAR_EN_COURS = new SensorState<>("RECALAGE_LIDAR_EN_COURS",false, Boolean.class);

    /**
     * Permet de désactiver le lidar à volonté (par exemple pendant une rotation)
     */
    public static final SensorState<Boolean> DISABLE_LIDAR = new SensorState<>("DISABLE_LIDAR",false, Boolean.class);

    /**
     * Permet de désactiver les obstacles de l'autre côté de la table. ça permet d'ignorer les ennemis lorsque le principal va au niveau de la balance
     */
    public static final SensorState<Boolean> DISABLE_ENNEMIES_OTHER_SIDE = new SensorState<>("DISABLE_ENNEMIES_OTHER_SIDE",false, Boolean.class);
    ;

    private String name;
    /**
     * La donnée
     */
    private DataType data;

    /**
     * La classe de la donnée : évite un transtypage après instanciation
     */
    private Class<DataType> c;

    /**
     * Construit un SensorState
     */
    private SensorState(String name, DataType object, Class<DataType> c) {
        this.name = name;
        this.data = object;
        this.c = c;
    }

    /**
     * Renvoie la valeur
     */
    public synchronized DataType getData() {
        return this.data;
    }

    /**
     * Set la valeur : package-private pour éviter qu'autre chose que le controller écrit les données
     * @param object    valeur à écrire
     * @throws ClassCastException   si l'on essai d'assigner à un paramètre un objet d'une autre classe
     */
    public synchronized void setData(DataType object) {
        if (object.getClass() != this.c) {
            throw new ClassCastException("Cette donnée est de type " + this.c + ", trouvé : " + object.getClass());
        }
        if(object != data) {
            Log.ORDERS.debug("SensorState."+name+" = "+object, 1);
        }
        this.data = object;
    }

    /**
     * Donnes l'objet {@link SensorState} correspondant au bras donné, ou throw {@link IllegalArgumentException} si le bras donné n'est pas "left" ou "right"
     * <hr/>
     * <b><i>ATTENTION</i></b>: Cette méthode demande le bras <b>physique</b>, ie ne prend pas en compte la symétrie
     * @param side le côté
     * @return le {@link SensorState} correspondant
     */
    public static SensorState<Boolean> getArmMovingState(String side) {
        switch (side) {
            case "left":
                return LEFT_ARM_MOVING;

            case "right":
                return RIGHT_ARM_MOVING;

            default:
                throw new IllegalArgumentException(side);
        }
    }
}
