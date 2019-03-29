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

import java.util.concurrent.CompletableFuture;

/**
 * Liste les données du robot et leurs état
 *
 * @author william
 */
public class SensorState<DataType> {
    public static final SensorState<Double> EXEMPLE = new SensorState<>(1.5, Double.class);
    public static final SensorState<Integer> CUBE_PRIS = new SensorState<>(0, Integer.class);
    public static final SensorState<Boolean> MOVING = new SensorState<>(false, Boolean.class);
    public static final SensorState<Boolean> STUCKED = new SensorState<>(false, Boolean.class);
    public static final SensorState<Boolean> LEFT_ELEVATOR_MOVING = new SensorState<>(false, Boolean.class);
    public static final SensorState<Boolean> RIGHT_ELEVATOR_MOVING = new SensorState<>(false, Boolean.class);
    public static final SensorState<Boolean> ACTUATOR_ACTUATING = new SensorState<>(false, Boolean.class);
    public static final SensorState<Long> LAST_PONG = new SensorState<>(-1L, Long.class);
    ;

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
    private SensorState(DataType object, Class<DataType> c) {
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
        this.data = object;
    }
}
