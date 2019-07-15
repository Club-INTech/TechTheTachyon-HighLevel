package simulator;

import data.CouleurPalet;
import data.XYO;
import robot.Robot;
import utils.HLInstance;
import utils.RobotSide;
import utils.container.ContainerException;
import utils.math.Vec2;

import java.util.List;

/**
 * Robot visualisé par le simulateur
 */
public class VisualisedRobot implements IRobot {

    private final HLInstance hl;
    private final Robot robot;

    public VisualisedRobot(HLInstance hl, Class<? extends Robot> robotClass) throws ContainerException {
        this.hl = hl;
        robot = hl.module(robotClass);
    }

    @Override
    public int getX() {
        return XYO.getRobotInstance().getPosition().getX();
    }

    @Override
    public int getY() {
        return XYO.getRobotInstance().getPosition().getY();
    }

    @Override
    public double getOrientation() {
        return XYO.getRobotInstance().getOrientation();
    }

    @Override
    public int getPort() {
        return SimulatedConnectionManager.VISUALISATION_PORT;
    }

    @Override
    public Vec2 getPosition() {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public Vec2 getTargetPosition() {
        return XYO.getRobotInstance().getPosition(); // TODO
    }

    @Override
    public String getLeftArmPosition() {
        return "unknown"; // TODO
    }

    @Override
    public String getRightArmPosition() {
        return "unknown"; // TODO
    }

    @Override
    public List<CouleurPalet> getElevatorOrNull(RobotSide side) {
        switch (side) {
            case LEFT:
                return robot.getLeftElevatorOrNull();

            case RIGHT:
                return robot.getRightElevatorOrNull();

            default:
                throw new RuntimeException("ça ne devrait pas arriver (side="+side+")");
        }
    }

    @Override
    public void setElevatorContents(RobotSide side, String[] contents, int startIndex) {/* noop */}
}
