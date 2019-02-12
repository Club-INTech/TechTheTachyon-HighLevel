package ai;

import ai.goap.ActionGraph;
import ai.goap.Agent;
import ai.goap.EnvironmentInfo;
import data.XYO;
import locomotion.Locomotion;
import locomotion.UnableToMoveException;
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
    }

    @Override
    protected void onPlanUpdated(EnvironmentInfo info) {
        spectre.comeBackToReality(); // resynchronise le spectre sur les valeurs réelles
    }

    @Override
    protected EnvironmentInfo gatherEnvironmentInformation() {
        return info;
    }

    @Override
    protected void orderMove(Vec2 position) {
        try {
            locomotion.moveToPoint(position);
        } catch (UnableToMoveException unableToMove) {
            unableToMove.printStackTrace();
            reportMovementError(unableToMove); // on transmet l'erreur au système de déplacement de l'agent
        }
    }
}
