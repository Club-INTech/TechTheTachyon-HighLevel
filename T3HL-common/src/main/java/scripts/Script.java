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
import data.table.MobileCircularObstacle;
import locomotion.UnableToMoveException;
import pfg.config.Config;
import robot.Robot;
import utils.ConfigData;
import utils.Log;
import utils.TimeoutError;
import utils.container.Module;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 * Définition d'un script :
 *
 * @author rem
 */
public abstract class Script implements Module {

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
     * Timeout avant d'arrêter d'essayer de se déplacer (quand un ennemi est dans un obstacle par exemple)
     */
    private int blockTimeout;

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
    public void goToThenExecute(Integer version) throws TimeoutError {
        Log.STRATEGY.debug("Executing script "+getClass().getCanonicalName()+" v"+version);
        Vec2 entryPosition = this.entryPosition(version);
        if (table.isPositionInFixedObstacle(entryPosition)) {
            // TODO Si le point trouvé est dans un obstacle fixe
        } else {
            Optional<MobileCircularObstacle> obstacle = table.findMobileObstacleInPosition(entryPosition);
            obstacle.ifPresent(mobileObstacle -> {
                Log.LOCOMOTION.warning("Point d'arrivée " + entryPosition + " dans l'obstacle mobile " + mobileObstacle);
                Log.LOCOMOTION.warning("Attente de "+blockTimeout+" ms tant que ça se libère pas...");

                // attente de qq secondes s'il y a un ennemi là où on veut aller
                try {
                    Module.withTimeout(blockTimeout, () -> {
                        while (table.isPositionInMobileObstacle(entryPosition)) {
                            try {
                                Thread.sleep(50);
                                Log.TABLE.critical("Robot in mobile obstacle");
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                } catch (TimeoutError e) {
                    e.printStackTrace();
                }
            });
        }

        try {
            async("Execution des actions pendant le déplacement du script "+getClass().getSimpleName()+" v"+version, () -> executeWhileMovingToEntry(version));
            this.robot.followPathTo(entryPosition);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
            if( ! shouldContinueScript(e)) {
                return;
            }
            // TODO
        }

        Exception exception = null;
        try {
            this.timedExecute(version);
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
            throw e;
            // TODO
        } finally {
            this.finalize(exception);
        }
        Log.STRATEGY.debug("End of script "+getClass().getCanonicalName()+" v"+version);
    }

    /**
     * Doit-on continuer l'exécution du script alors qu'on a pas réussi à y aller?
     * <br/>
     * ATTENTION: finalize() ne sera PAS appelé si vous renvoyez false!
     * @param e l'erreur
     * @return 'true' si on continue, 'false' sinon
     */
    protected boolean shouldContinueScript(Exception e) {
        return true;
    }

    /**
     * Exécution d'actions pendant le mouvement jusqu'à la position d'entrée du script. Utile pour mettre les bras à la bonne position, baisser un ascenseur, etc.
     * @param version la version du script
     */
    public void executeWhileMovingToEntry(int version) {}

    /**
     * Executes le script et affiche le temps de départ, fin et durée du script
     * @param version
     */
    public final void timedExecute(int version) {
        long startTime = System.currentTimeMillis()-Log.getStartTime();
        Log.STRATEGY.warning("Starting script "+getClass().getSimpleName()+" v"+version+" at "+ formatTime(startTime));
        execute(version);
        long endTime = System.currentTimeMillis()-Log.getStartTime();
        Log.STRATEGY.warning("Ending script "+getClass().getSimpleName()+" v"+version+" at "+ formatTime(endTime));
        long elapsed = endTime-startTime;
        Log.STRATEGY.warning("Script "+getClass().getSimpleName()+" v"+version+" took "+ formatTime(elapsed));
    }

    protected String formatTime(long time) {
        return String.format("%03d", time / 1000) + "." + String.format("%03d", time % 1000);
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
    public abstract Vec2 entryPosition(Integer version);

    /**
     * A executer à la fin du script, terminé ou non !
     * @param e
     *              l'exception qui a été levée
     */
    public abstract void finalize(Exception e);

    protected CompletableFuture<Void> async(Runnable action) {
        return async("Async Thread - "+action, action);
    }

    protected CompletableFuture<Void> async(String name, Runnable action) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Thread thread = new Thread(() -> {
            action.run();
            future.complete(null);
            Log.STDOUT.critical("Fin async "+name);
        });
        thread.setName(name+" (async)");
        thread.setDaemon(true);
        thread.start();
        return future;
    }

    @Override
    public void updateConfig(Config config) {
        blockTimeout = config.getInt(ConfigData.LOCOMOTION_OBSTRUCTED_TIMEOUT);
    }
}
