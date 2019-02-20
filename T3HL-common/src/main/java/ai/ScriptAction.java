/**
 * Copyright (c) 2019, INTech.
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

package ai;

import ai.goap.Action;
import ai.goap.EnvironmentInfo;
import data.Graphe;
import data.Table;
import data.graphe.Node;
import data.table.Obstacle;
import locomotion.NoPathFound;
import scripts.Script;
import scripts.ScriptNames;
import utils.Log;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.Optional;

public class ScriptAction extends Action {

    /**
     * Le script à exécuter
     */
    private final Script script;

    /**
     * Le nom du script
     */
    private final ScriptNames scriptName;

    /**
     * Coût de base du script (sans compter le déplacement vers son entryPosition)
     */
    private final double baseCost;

    /**
     * La version du script à exécuter
     */
    private final int version;

    /**
     * Le script a-t-il été exécuté?
     */
    private boolean executed;

    public ScriptAction(ScriptNames scriptName, int version) {
        this(scriptName, version, 0.0);
    }

    public ScriptAction(ScriptNames scriptName, int version, double baseCost) {
        this.baseCost = baseCost;
        this.version = version;
        this.scriptName = scriptName;
        this.script = scriptName.getScript();
    }

    @Override
    public boolean arePreconditionsMet(EnvironmentInfo info) {
        return !executed && super.arePreconditionsMet(info) && checkPath(info);
    }

    private boolean checkPath(EnvironmentInfo info) {
        boolean result = false;
        Graphe graph = info.getSpectre().getSimulatedGraph();
            Node start = null;
            Node aim = null;
            try {

                Vec2 entryPos = script.entryPosition(version).getCenter();
                // TODO: vérif des locks & finally
     //           graph.writeLock().lock();
                Vec2 currentPos = info.getCurrentPosition();
                start = graph.addProvisoryNode(currentPos);
                aim = graph.addProvisoryNode(entryPos);
                Table table = info.getSpectre().getSimulatedTable();

                Optional<Obstacle> obstacleBelowPosition = table.findFixedObstacleInPosition(info.getXYO().getPosition());
                if(obstacleBelowPosition.isPresent()) {
             //       Log.LOCOMOTION.warning("Points de départ " + info.getXYO().getPosition() + " dans l'obstacle " + obstacleBelowPosition.get());
                    // graph.writeLock().unlock();
                    return false;
                }
                Optional<Obstacle> obstacleBelowPoint = table.findFixedObstacleInPosition(entryPos);
                if(obstacleBelowPoint.isPresent()) {
                //    Log.LOCOMOTION.warning("Points d'arrivée " + entryPos + " dans l'obstacle " + obstacleBelowPoint.get());
                    //graph.writeLock().unlock();
                    return false;
                }

             //   graph.writeLock().unlock();
                try {
                    info.getSpectre().getSimulationPathfinder().findPath(start, aim);
                    result = true;
                } catch (NoPathFound f) {
    /*        System.out.println(">> "+toString());
            f.printStackTrace(); // TODO: debug only*/
                }
            } finally {
                try {
               //     graph.writeLock().lock();
                  /*  if(start != null)
                        graph.removeProvisoryNode(start);
                    if(aim != null)
                        graph.removeProvisoryNode(aim);*/
                } finally {
                 //   graph.writeLock().unlock();
                }
            }
        return result;
    }

    @Override
    public double getCost(EnvironmentInfo info) {
        Vec2 entryPos = script.entryPosition(version).getCenter();
        return baseCost + info.getXYO().getPosition().squaredDistanceTo(entryPos) + Math.abs(info.getXYO().getPosition().angleTo(entryPos));
    }

    @Override
    public void perform(EnvironmentInfo info) {
        script.goToThenExecute(version);
        executed = true;
    }

    @Override
    public boolean isComplete(EnvironmentInfo info) {
        return executed;
    }

    @Override
    public boolean requiresMovement(EnvironmentInfo info) {
        return true;
    }

    @Override
    public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {
        Vec2 entryPos = script.entryPosition(version).getCenter();
        targetPos.set(entryPos);
    }

    @Override
    public void reset() {

    }

    @Override
    public String toString() {
        return "ScriptAction("+scriptName+")";
    }
}
