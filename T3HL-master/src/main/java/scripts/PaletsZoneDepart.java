package scripts;

import data.CouleurPalet;
import data.SensorState;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class PaletsZoneDepart extends Script {

    private static final int DISTANCE_INTERPALET = 300;
    private final int xEntry = 1500-191-65;//1350;
    private final int yEntry = 450;
    private final Vec2[] positions = new VectCartesian[]{
            new VectCartesian(xEntry, yEntry),
            new VectCartesian(xEntry,yEntry+302),
            new VectCartesian(xEntry,yEntry+605)
    };

    public PaletsZoneDepart(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        boolean premierPaletPris = false;
        int i =0;
        switch (version){
            case  0 : //version qui prend les 3 palets
                try {
                    robot.turn(Math.PI / 2);
                    robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, true); // on attent que le vide se fasse
                    // robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                    for (Vec2 position : positions) {
                        if (premierPaletPris) {
                            // SensorState.LEFT_ELEVATOR_MOVING.setData(true);
                            //robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET,false);
                            robot.followPathTo(position,() -> {robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET, false);});
                            robot.turn(Math.PI / 2);
                            //waitWhileTrue(SensorState.LEFT_ELEVATOR_MOVING::getData);
                            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET,false);
                        } else {
                            premierPaletPris = true;
                        }
                        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE,false);
                        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, false);
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL,true);
                        //Attend avant de prendre un palet
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,true);
                        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE,false);
                        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true); // on attend que le vide se casse
                        robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
                        robot.waitForLeftElevator();
                        //Attend avant de prendre un palet
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR,false);
                        //robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                        //robot.waitForLeftElevator();
                        //il vaut mieux enlever les obstacles en même temps que attendre d'enlever les 3 nn ?
                        switch (i) {
                            case 0:
                                robot.pushPaletGauche(CouleurPalet.ROUGE);
                                table.removeTemporaryObstacle(table.getPaletRougeDroite());
                                i++;
                                break;
                            case 1:
                                robot.pushPaletGauche(CouleurPalet.ROUGE);
                                table.removeTemporaryObstacle(table.getPaletRougeDroite());
                                i++;
                                break;
                            case 2:
                                robot.pushPaletGauche(CouleurPalet.VERT);
                                table.removeTemporaryObstacle(table.getPaletVertDroite());
                                i++;
                                break;
                        }
                    }
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET, false); //on descend l'ascenceur à la fin du script
                    //robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                    //robot.waitForLeftElevator();
                    //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET,false);
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                }
                    break;
            case 1 ://version qui prend qu'un seul palet
                try {
                    for (int j = 0; j < 2; j++) {
                        if (premierPaletPris) {
                            robot.turn(Math.PI);
                            robot.gotoPoint(new VectCartesian(xEntry - 190, yEntry + 605));
                        } else {
                            robot.turn(Math.PI / 2);
                            premierPaletPris = true;
                        }
                        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, true);
                        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, false);
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL, true);
                        //Attend avant de prendre un palet
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT, true);
                        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE, false);
                        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true); // on attend que le vide se casse
                        robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
                        robot.waitForLeftElevator();
                        switch (i) {
                            case 0:
                                robot.pushPaletGauche(CouleurPalet.VERT);
                                table.removeTemporaryObstacle(table.getPaletVertDroite());
                                i++;
                                break;
                            case 1:
                                robot.pushPaletGauche(CouleurPalet.ROUGE);
                                table.removeTemporaryObstacle(table.getPaletRougeDroite());

                                break;
                        }

                    }
                    break;
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                }
        }

    }

    @Override
    public Vec2 entryPosition(Integer version) {
        if(version ==1)//position du premier palet
        {
            return new VectCartesian(xEntry, yEntry+605);
        }
        else{
            return new VectCartesian(xEntry, yEntry);
        }
    }


    @Override
    public void finalize(Exception e) {
        // range le bras quand on a fini
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,false);
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
