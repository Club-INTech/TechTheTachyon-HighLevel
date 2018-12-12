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

package scripts;

import orders.hooks.HookFactory;
import robot.Robot;
import utils.container.Service;
import utils.math.Shape;

import java.util.ArrayList;

/**
 * Définition d'un script :
 *
 * @author rem
 */
public abstract class Script implements Service {

    /**
     * Le robot
     */
    protected Robot robot;

    /**
     * La hook factory
     */
    protected HookFactory hookFactory;

    /**
     * Les différentes versions du script
     */
    protected ArrayList<Integer> versions;

    /**
     * Construit un script
     * @param robot
     *              le robot
     * @param hookFactory
     *              la hookFactory
     */
    protected Script(Robot robot, HookFactory hookFactory) {
        this.robot = robot;
        this.hookFactory = hookFactory;
    }

    /**
     * Méthode d'execution du script : permet d'aller à l'entry position du script et de l'executer
     * @param version
     *              version du script à executer
     */
    public void goToThenExecute(Integer version) {
        /*
         * On commence par trouver le point d'entrer à l'aide de l'entryPosition
         * Si non trouvé ou obstrué
         *      Exception levée
         * On tente d'aller à ce point
         *      Si Exception
         *          On retente de trouver un point d'entré
         *              Si non trouvé ou obstrué
         *                  Exception levéé
         *              Sinon goTo "On tente d'aller à ce point"
         *      Sinon
         *          On continu
         * Execution du script
         *      Si Exception, rethrow
         */
    }

    /**
     * Methode d'execution du script !
     * @param version
     *              la version à executer
     */
    public abstract void execute(Integer version);

    /**
     * Methode retournant la zone d'entrée, c'est-à-dire une forme !
     * @param version
     *              la version à executer
     * @return  le perimètre d'entré
     */
    public abstract Shape entryPosition(Integer version);

    /**
     * A executer à la fin du script, terminé ou non !
     * @param e
     *              l'exception qui a été levée
     */
    public abstract void finalize(Exception e);
}
