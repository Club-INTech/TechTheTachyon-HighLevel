package scripts;

import data.CouleurPalet;
import data.Sick;
import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PaletsZoneDepart extends Script {

    /**
     * Version qui ne prend que le palet devant la case bleue
     */
    public static int JUST_BLUE = 1;

    private static final int DISTANCE_INTERPALET = 300;
    private final int xEntry = 1500-191-65;//1244;
    private final int yEntry = 450+605;//;

    public PaletsZoneDepart(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        Vec2[] positions;
        Vec2 entry = entryPosition(version);
        if(version == JUST_BLUE) {
            positions = new VectCartesian[]{
                    new VectCartesian(entry.getX(), entry.getY()),
                    //new VectCartesian(xEntry-190-46, yEntry-15),
                    new VectCartesian(xEntry,yEntry+302),
                    //new VectCartesian(xEntry,yEntry+605)
            };
        } else {
            positions = new VectCartesian[]{
                    //new VectCartesian(xEntry, yEntry),
                    new VectCartesian(entry.getX(), entry.getY()),
                    new VectCartesian(entry.getX(),1050),
                    //new VectCartesian(entry.getX(),entry.getY()+605),
            };
        }
        boolean premierPaletPris = false;
        int i =0;
        try {
            //robot.turn(Math.PI / 2);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, true); // on attent que le vide se fasse
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
            // robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
            CompletableFuture<Void> puckStored = null;
            CompletableFuture<Void> elevatorAtRightPlace = null;
            for (Vec2 position : positions) {
                CompletableFuture<Void> armInPlace = null;
                if(premierPaletPris&&version==JUST_BLUE){
                    robot.turn(Math.PI);
                    robot.computeNewPositionAndOrientation(Sick.UPPER_RIGHT_CORNER_TOWARDS_PI);
                    robot.followPathTo(position,() -> robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET, false));
                }
                else if (premierPaletPris) {
                    robot.followPathTo(position);
                    robot.turn(Math.PI / 2);
                } else {
                    premierPaletPris = true;
                }
                if(puckStored != null) {
                    puckStored.join();
                }
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL,true);

                int puckIndex = i; // on est obligés de copier la variable pour la transmettre à la lambda
                puckStored = async("Remonte vers ascenseur et recale", () -> {
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,true);
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                    if(puckIndex == 0) { // on retourne au sol que pour le 2e palet
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET,true);
                        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                    }
                });

                CompletableFuture<Void> finalPuckStored1 = puckStored;
                elevatorAtRightPlace = async("Recalage ascenseur", () -> {
                    if(finalPuckStored1 != null) {
                        finalPuckStored1.join();
                    }
                    readjustElevator(puckIndex);
                });

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

            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, false);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recalage du palet dans l'ascenseur et fin de dépôt
     * @param puckIndex indice du palet (premier est 0)
     */
    private void readjustElevator(int puckIndex) {
        if(puckIndex == 0) { // 1er palet
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET, false);
        } else { // 2e palet
            robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET,false);
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        if(version ==JUST_BLUE)//position du premier palet
        {
            return new VectCartesian(xEntry, yEntry);
        }
        else{
            //return new VectCartesian(xEntry, yEntry-605);
            return XYO.getRobotInstance().getPosition();
        }
    }


    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
