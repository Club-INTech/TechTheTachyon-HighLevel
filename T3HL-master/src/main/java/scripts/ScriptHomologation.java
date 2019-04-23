package scripts;

import data.CouleurPalet;
import data.Table;
import data.table.Obstacle;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import robot.Robot;
import utils.Log;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
import java.util.List;

public class ScriptHomologation extends Script {

    private static final int DISTANCE_INTERPALET = 300;

    private int xEntry = 1500-191-65+20;//1350;
    private int yEntry = 430;
    private Vec2[] positions = new VectCartesian[]{
            new VectCartesian(xEntry, yEntry),
            new VectCartesian(xEntry,yEntry+300),
            new VectCartesian(xEntry,yEntry+600)
    };

    public ScriptHomologation(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        boolean premierPaletPris = false;
        try {
            robot.turn(Math.PI/2);
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
            robot.turn(Math.PI);
            robot.moveLengthwise(DISTANCE_INTERPALET*3, false);
            robot.turn(-Math.PI/2);
            robot.moveLengthwise(DISTANCE_INTERPALET, false);
            robot.turn(0);
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
            robot.turn(Math.PI);
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
            robot.turn(-Math.PI/2);
            robot.moveLengthwise(DISTANCE_INTERPALET, false);
            robot.turn(0);
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);


        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Shape entryPosition(Integer version) {
        return new Circle(new VectCartesian(xEntry, yEntry), 5);
    }

    @Override
    public void finalize(Exception e) {
    }

    @Override
    public void updateConfig(Config config) {
    }
}
