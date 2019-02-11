package ai;

import ai.goap.ActionGraph;
import ai.goap.Agent;
import ai.goap.EnvironmentInfo;
import data.XYO;
import locomotion.Locomotion;
import locomotion.UnableToMoveException;
import robot.Robot;
import utils.math.Vec2;

import java.util.HashMap;

public class RobotAgent extends Agent {
    private final EnvironmentInfo info;
    private final Locomotion locomotion;

    public RobotAgent(Locomotion locomotion, ActionGraph graph) {
        super(graph);
        this.locomotion = locomotion;
        this.info = new EnvironmentInfo(XYO.getRobotInstance(), new HashMap<>());

        // TODO: init de 'state' dans 'info'

        info.getState().put("PaletsX6", false);
        info.getState().put("PaletsX3", false);
    }

    @Override
    protected EnvironmentInfo gatherEnvironmentInformation() {
        return info;
    }

    @Override
    protected void orderMove(Vec2 position) {
        try {
            locomotion.moveToPoint(position);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
            reportMovementError(e); // on transmet l'erreur au système de déplacement de l'agent
        }
    }
}
