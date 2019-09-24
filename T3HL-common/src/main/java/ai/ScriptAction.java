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
import ai.goap.ActionGraph;
import ai.goap.EnvironmentInfo;
import data.Graphe;
import data.Table;
import data.graphe.Node;
import data.table.Obstacle;
import locomotion.NoPathFound;
import robot.Robots;
import scripts.Script;
import scripts.ScriptNames;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;

import java.util.HashSet;
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
        Script scriptInstance;
        try {
            // TODO FIXME
            scriptInstance = scriptName.createScript(HLInstance.get(Robots.MAIN));
        } catch (ContainerException e) {
            e.printStackTrace();
            scriptInstance = null;
        }
        this.script = scriptInstance;
    }

    @Override
    public boolean arePreconditionsMet(EnvironmentInfo info) {
        return !executed && super.arePreconditionsMet(info) && checkPath(info);
    }

    private boolean checkPath(EnvironmentInfo info) {
        try {
            long startTime = System.nanoTime();
            boolean result = false;
            Graphe graph = info.getSpectre().getSimulatedGraph();
            Vec2 entryPos = script.entryPosition(version);
            Vec2 currentPos = info.getCurrentPosition();
            Node start = graph.addProvisoryNode(currentPos);
            Node aim = graph.addProvisoryNode(entryPos);
            Table table = info.getSpectre().getSimulatedTable();

            Optional<Obstacle> obstacleBelowPosition = table.findFixedObstacleInPosition(info.getXYO().getPosition());
            if(obstacleBelowPosition.isPresent()) {
                return false;
            }
            Optional<Obstacle> obstacleBelowPoint = table.findFixedObstacleInPosition(entryPos);
            if(obstacleBelowPoint.isPresent()) {
                return false;
            }

            try {
                info.getSpectre().getSimulationPathfinder().findPath(start, aim, new HashSet<>());
                result = true;
            } catch (NoPathFound f) {
            }
            ActionGraph.pathfinderProfiler.getAndAdd(System.nanoTime()-startTime);
            return result;
        } finally {
        }
    }

    @Override
    public double getCost(EnvironmentInfo info) {
        Vec2 entryPos = script.entryPosition(version);
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
        Vec2 entryPos = script.entryPosition(version);
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
