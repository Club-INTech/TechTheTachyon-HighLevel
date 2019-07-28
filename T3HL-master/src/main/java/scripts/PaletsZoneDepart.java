package scripts;

import data.CouleurPalet;
import data.Sick;
import data.XYO;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import utils.HLInstance;
import utils.Offsets;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.Future;

public class PaletsZoneDepart extends Script implements Offsets {

    /**
     * Version qui ne prend que le palet devant la case bleue
     */
    public static int JUST_BLUE = 1;

    private static final int DISTANCE_INTERPALET = 300;
    private final int xEntry = 1500-191-65+ Offsets.get(ZDD_X_VIOLET);//1244;
    private final int yEntry = 450+605;//;

    @Configurable
    private boolean symetry;

    public PaletsZoneDepart(HLInstance hl) {
        super(hl);
    }

    @Override
    public void execute(int version) {
        Vec2[] positions;
        Vec2 entry = entryPosition(version);
        //double OffX = (container.getConfig().getString(ConfigData.COULEUR).equals("jaune") ? PALETS_DEPART_X_JAUNE : GOLDENIUM_X_VIOLET).get();
        double offX;
        if (symetry){
            offX= Offsets.get(ZDD_X_VIOLET);
        } else {
            offX= Offsets.get(ZDD_X_JAUNE);
        }
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
                    new VectCartesian(xEntry+offX, entry.getY()),
                    new VectCartesian(xEntry+offX-20,1040),
                    //new VectCartesian(entry.getX(),entry.getY()+605),
            };
        }
        boolean premierPaletPris = false;
        int i =0;
        try {
            //robot.turn(Math.PI / 2);
            actuators.leftPump.activate(true);
            actuators.leftValve.deactivate(true);
            // robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
            Future<Void> puckStored = null;
            for (Vec2 position : positions) {
                if(premierPaletPris&&version==JUST_BLUE) {
                    turn(Math.PI);
                    robot.computeNewPositionAndOrientation(Sick.UPPER_RIGHT_CORNER_TOWARDS_PI);
                    async("Descend ascenseur gauche", () -> actuators.leftElevator.down());
                    robot.followPathTo(position);
                }
                else if (premierPaletPris) {
                    robot.followPathTo(position);
                    turn(Math.PI / 2);
                } else {
                    premierPaletPris = true;
                }
                join(puckStored);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL,true);

                int puckIndex = i; // on est obligés de copier la variable pour la transmettre à la lambda
                puckStored = async("Remonte vers ascenseur et recale", () -> {
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,true);
                    actuators.leftValve.activate(true);
                    readjustElevator(puckIndex);
                    if(puckIndex == 0) { // on retourne au sol que pour le 2e palet
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET,true);
                        actuators.leftValve.deactivate(true);
                    }
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
            actuators.leftElevator.down();
        } else { // 2e palet
            actuators.leftElevator.downup();
        }
    }

    @Override
    public Vec2 entryPosition(int version) {
        if(version ==JUST_BLUE)//position du premier palet
        {
            return new VectCartesian(xEntry, yEntry);
        }
        else{
            //return new VectCartesian(xEntry + (container.getConfig().getString(ConfigData.COULEUR).equals("jaune") ? PALETS_DEPART_X_JAUNE : GOLDENIUM_X_VIOLET).get(), yEntry-605);
            return XYO.getRobotInstance().getPosition();
        }
    }


    @Override
    public void finalize(Exception e) { }
}
