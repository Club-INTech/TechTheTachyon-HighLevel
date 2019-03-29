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

import ai.goap.ActionGraph;
import ai.goap.Agent;
import ai.goap.EnvironmentInfo;
import data.XYO;
import locomotion.Locomotion;
import locomotion.UnableToMoveException;
import utils.Log;
import utils.math.Vec2;

import java.util.HashMap;

public class RobotAgent extends Agent {
    private final EnvironmentInfo info;
    private final Locomotion locomotion;
    private final SpectreRobot spectre;

    public RobotAgent(Locomotion locomotion, ActionGraph graph, SpectreRobot spectre) {
        super(graph);
        this.spectre = spectre;
        this.locomotion = locomotion;
        this.info = new EnvironmentInfo(XYO.getRobotInstance(), new HashMap<>(), spectre);

        // TODO: init de 'state' dans 'info'

        info.getState().put("PaletsX6", false);
        info.getState().put("PaletsX3", false);
        info.getState().put("ZoneDepart", false);
        info.getState().put("Accelerateur", false);
        info.getState().put("movesDone", 0);
    }

    @Override
    protected void onPlanUpdated(EnvironmentInfo info) {
        if(getCurrentPlan() != null) {
            Log.AI.debug("Nouveau plan:");
            for(ActionGraph.Node actionNode: getCurrentPlan()) {
                Log.AI.debug("\t- "+actionNode.getAction());
            }
        }
        spectre.comeBackToReality(); // resynchronise le spectre sur les valeurs réelles
    }

    @Override
    protected EnvironmentInfo gatherEnvironmentInformation() {
        return info;
    }

    @Override
    protected void orderMove(Vec2 position) {
        try {
            locomotion.followPathTo(position);
        } catch (UnableToMoveException unableToMove) {
            unableToMove.printStackTrace();
            reportMovementError(unableToMove); // on transmet l'erreur au système de déplacement de l'agent
        }
    }
}
