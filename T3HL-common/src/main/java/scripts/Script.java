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

import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import robot.Robot;
import utils.container.Service;
import utils.math.Shape;
import utils.math.Vec2;

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
     * La table
     */
    protected Table table;

    /**
     * Les différentes versions du script
     */
    protected ArrayList<Integer> versions;

    /**
     * Construit un script
     * @param robot
     *              le robot
     */
    protected Script(Robot robot, Table table) {
        this.robot = robot;
        this.table = table;
    }

    /**
     * Méthode d'execution du script : permet d'aller à l'entry position du script et de l'executer
     * @param version
     *              version du script à executer
     */
    public void goToThenExecute(Integer version) {
        Vec2 entryPosition = this.entryPosition(version).closestPointToShape(XYO.getRobotInstance().getPosition());
        if (table.isPositionInFixedObstacle(entryPosition)) {
            // TODO Si le point trouvé est dans un obstacle fixe
        } else if (table.isPositionInMobileObstacle(entryPosition)) {
            // TODO Si le point trouvé est dans un obstacle mobile
        }

        try {
            this.robot.followPathTo(entryPosition, () -> this.executeWhileMovingToEntry(version));
        } catch (UnableToMoveException e) {
            e.printStackTrace();
            // TODO
        }

        try {
            this.execute(version);
        } catch (Exception e) {
            this.finalize(e);
            e.printStackTrace();
            throw e;
            // TODO
        }
    }

    /**
     * Exécution d'actions pendant le mouvement jusqu'à la position d'entrée du script. Utile pour mettre les bras à la bonne position, baisser un ascenseur, etc.
     * @param version la version du script
     */
    public void executeWhileMovingToEntry(int version) {}

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
