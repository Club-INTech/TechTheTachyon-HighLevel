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

package orders;

/**
 * Définition des vitesses possibles de déplacement du robot.
 * Les deux arguments passés dans les vitesses correspondent à des valeurs en mm/s pour la translation puis en rad/s pour la rotation
 *
 * @author yousra
 */

public enum Speed {
    //TODO régler les valeurs de vitesse en translations et rotations avec des phases de test, décider des combinaisons de vitesses inutiles
    // TODO: Le LL devrait aller plus vite cette année, faut prendre en compte ça aussi

    /** Vitesse ultra lente en translation, ultra lente en rotation */
    ULTRA_VERY_SLOW_ALL(10,0.5D),

    ULTRA_SLOW_ALL(100,0.5D),

    /** Vitesse très lente en translation, très lente en rotation */
    VERY_SLOW_ALL(150,0.75D),

    /** Vitesse lente en translation, lente en rotation */
    SLOW_ALL(250, 1*Math.PI),

    /** Vitesse standard de déplacement et rotation */
    MEDIUM_ALL(400,2D),

    /** Vitesse rapide en translation et rotation */
    FAST_ALL(500,2*Math.PI),

    /** Vitesse ultra rapide en translation et rotation
     *  TODO: NON TESTEE
     */
    ULTRA_FAST_ALL(1000,4D),

    /** Vitesse par défaut (utliser une des vitesses au dessus dans les parenthèses)
     *  Fait pour ne pas avoir à changer la vitesse dans tout le code
     */
    DEFAULT_SPEED(FAST_ALL);

    /**
     * Vitesse de translation du robot - envoyé au LL via l'OrderWrapper
     */
    private int translationSpeed;

    /**
     * Vitesse de rotation du roboy - envoyé au LL via l'OrderWrapper
     */
    private double rotationSpeed;

    /**
     * Constructeur d'une vitesse.
     * @param translationSpeed la vitesse de translation ( en mm/s)
     * @param rotationSpeed la vitesse de rotation (en rad/s)
     */
    Speed(int translationSpeed, double rotationSpeed) {
        this.translationSpeed = translationSpeed;
        this.rotationSpeed = rotationSpeed;
    }

    /**
     * Constructeur pour set une vitesse par défaut
     * @param speedOrder : la vitesse qu'on veut
     */
    Speed(Speed speedOrder) {
        this.translationSpeed= speedOrder.getTranslationSpeed();
        this.rotationSpeed= speedOrder.getRotationSpeed();
    }

    /**
     * Getters
     */
    public int getTranslationSpeed() {
        return translationSpeed;
    }
    public double getRotationSpeed() {
        return rotationSpeed;
    }
}


