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
import lowlevel.actuators.ActuatorsModule;
import pfg.config.Configurable;
import robot.Robot;
import utils.HLInstance;
import utils.Log;
import utils.TimeoutError;
import utils.container.Module;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;


/**
 * Définition d'un script
 * @author rem, jglrxavpok
 */
public abstract class Script implements Module {

    protected final HLInstance hl;

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
    @Configurable
    private long locomotionObstructedTimeout;

    @Configurable
    protected boolean symetry;

    protected ActuatorsModule actuators;

    /**
     * Construit un script
     * @param hl
     *              le container
     */
    protected Script(HLInstance hl) {
        this.hl = hl;
        this.robot = hl.module(Robot.class);
        this.table = hl.module(Table.class);
        this.actuators = hl.module(ActuatorsModule.class);
    }

    /**
     * Méthode d'execution du script : permet d'aller à l'entry position du script et de l'executer
     * @param version
     *              version du script à executer
     */
    public void goToThenExecute(int version) throws TimeoutError {
        Log.STRATEGY.debug("Executing script "+getClass().getCanonicalName()+" v"+version);
        Vec2 entryPosition = this.entryPosition(version);
        if (table.isPositionInFixedObstacle(entryPosition)) {
            // TODO Si le point trouvé est dans un obstacle fixe
        } else {
            Optional<MobileCircularObstacle> obstacle = table.findMobileObstacleInPosition(entryPosition);
            obstacle.ifPresent(mobileObstacle -> {
                Log.LOCOMOTION.warning("Point d'arrivée " + entryPosition + " dans l'obstacle mobile " + mobileObstacle);
                Log.LOCOMOTION.warning("Attente de "+ locomotionObstructedTimeout +" ms tant que ça se libère pas...");

                // attente de qq secondes s'il y a un ennemi là où on veut aller
                try {
                    Module.withTimeout(locomotionObstructedTimeout, () -> {
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
    public abstract void execute(int version);

    /**
     * Exécute la version 0 du script.
     */
    public final void execute() {
        execute(0);
    }

    /**
     * Exécute la version 0 du script.
     */
    public final void timedExecute() {
        timedExecute(0);
    }

    /**
     * Methode retournant la zone d'entrée, c'est-à-dire une forme !
     * @param version
     *              la version à executer
     * @return  le perimètre d'entré
     */
    public abstract Vec2 entryPosition(int version);

    /**
     * A executer à la fin du script, terminé ou non !
     * @param e
     *              l'exception qui a été levée
     */
    public abstract void finalize(Exception e);

    // =========================================
    // Début des méthodes pour raccourcir le code
    // =========================================

    /**
     * Tournes le robot vers un angle donné (absolu!)
     * @param angle l'angle absolu vers lequel le robot doit s'orienter
     */
    public void turn(double angle) throws UnableToMoveException {
        robot.turn(angle);
    }

    /**
     * Cf {@link #followPathTo(Vec2, int)} (valeur de maxRetries à -1)
     * @see #followPathTo(Vec2, int)
     */
    public void followPathTo(Vec2 point) throws UnableToMoveException {
        followPathTo(point, -1);
    }

    /**
     * Permet au robot d'aller jusqu'à un point donné. Actives les leds du robot en fonction de son état.<br/>
     * <ul>
     *     <li>Rouge: Première tentative de followpath</li>
     *     <li>Bleu: Nouvelle tentative de followpath (indice pair)</li>
     *     <li>Vert: Nouvelle tentative de followpath (indice impair)</li>
     * </ul>
     * @param point
     *              le point visé
     * @param maxRetries
     *              le nombre de réessais à faire, 0 throw un UnableToMoveException dès le premier échec, -1 signifie de tester à l'infini
     * @throws UnableToMoveException
     *              en cas de problème de blocage/adversaire
     */
    public void followPathTo(Vec2 point, int maxRetries) throws UnableToMoveException, TimeoutError {
        robot.followPathTo(point, maxRetries);
    }

    /**
     * Permet au robot d'avancer/recluer en ligne droite
     * @param distance
     *              la distance à parcourir, négative si l'on veut aller en arrière
     * @param expectedWallImpact
     *              true si l'on s'attend à un blocage mécanique (lorsque l'on veut se caler contre le mur par exemple)
     * @throws UnableToMoveException
     *              en cas de problèmes de blocage/adversaire
     */
    public void moveLengthwise(int distance, boolean expectedWallImpact, Runnable... runnables) throws UnableToMoveException {
        robot.moveLengthwise(distance, expectedWallImpact, runnables);
    }

        // =========================================
    // Fin des méthodes pour raccourcir le code
    // =========================================

    protected Future<Void> async(Runnable action) {
        return async("Async Thread - "+action, action);
    }

    protected Future<Void> async(String name, Runnable action) {
        return hl.async(name, action);
    }

    /**
     * Réessaies une même action, avec un nombre de réessai maximal
     * @param times le nombre de réessais max à tenter
     * @param action l'action à répéter. Doit renvoyer 'true' quand l'action est réussie
     * @return 'true' si l'action a été réussie, 'false' sinon
     */
    protected boolean attemptMultipleTimes(int times, Supplier<Boolean> action) {
        for (int i = 0; i < times; i++) {
            if(action.get()) {
                return true;
            }
        }
        return false;
    }

    protected void join(Future<Void> future) {
        if(future != null) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
