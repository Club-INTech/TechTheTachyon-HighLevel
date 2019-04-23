package scripts;

import data.CouleurPalet;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class Paletsx3 extends Script {
    /**
     * Position d'entrée du script
     */

    //Valeurs à ajuster pour le robot secondaire
    private int xEntry = 1375;
    private int yEntry = 1800 ;//+  (int) ConfigData.ROBOT_RAY.getDefaultValue() ;
    private int xEntrySymetrie= -1175;
    private int yEntrySymetrie= 1800;
    /**
     * constante
     */
    Vec2[] positions = new Vec2[]{
            new VectCartesian(xEntry,yEntry),
            new VectCartesian(xEntry-100,yEntry),
            new VectCartesian(xEntry-200,yEntry),
    };
    Vec2[] positionsSymetrie = new Vec2[]{
            new VectCartesian(xEntrySymetrie,yEntrySymetrie),
            new VectCartesian(xEntrySymetrie-100,yEntrySymetrie),
            new VectCartesian(xEntrySymetrie-200,yEntrySymetrie),
    };
    CouleurPalet couleursPaletsx3[] = {CouleurPalet.ROUGE, CouleurPalet.VERT, CouleurPalet.ROUGE};
    boolean symetrie;


    public Paletsx3(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            if(!symetrie) {
                robot.turn(Math.PI);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
                for (int j = 1; j < positions.length - 1; j++) {
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR, true);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
                    robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
                    //robot.useActuator(ActuatorsOrder.TEST_PALET_ATTRAPÉ_EN_FONCTION_DU_COUPLE_DROIT);
                    CouleurPalet couleur = couleursPaletsx3[j];
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
                    robot.pushPaletDroit(couleur); // TODO
                    robot.followPathTo(positions[j]);
                }
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR, true);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
                //robot.useActuator(ActuatorsOrder.TEST_PALET_ATTRAPÉ_EN_FONCTION_DU_COUPLE_DROIT);
                CouleurPalet couleur = CouleurPalet.getCouleurPalRecu();
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
                robot.pushPaletDroit(couleur); // TODO
                robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);
            }
            else{
                robot.turn(Math.PI);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
                for (int j = 1; j < positionsSymetrie.length - 1; j++) {
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR, true);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
                    robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
                    //robot.useActuator(ActuatorsOrder.TEST_PALET_ATTRAPÉ_EN_FONCTION_DU_COUPLE_DROIT);
                    CouleurPalet couleur = couleursPaletsx3[j];
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
                    robot.pushPaletDroit(couleur); // TODO
                    robot.followPathTo(positionsSymetrie[j]);
                }
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR, true);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
                //robot.useActuator(ActuatorsOrder.TEST_PALET_ATTRAPÉ_EN_FONCTION_DU_COUPLE_DROIT);
                CouleurPalet couleur = CouleurPalet.getCouleurPalRecu();
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
                robot.pushPaletDroit(couleur); // TODO
                robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);

            }

        }
        catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Shape entryPosition(Integer version) {
        if(symetrie){
            return new Circle(new VectCartesian(xEntrySymetrie, yEntrySymetrie),5);
        }
        return new Circle(new VectCartesian(xEntry, yEntry), 5);
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
    }
}
