package scripts;
import data.CouleurPalet;
import data.Sick;
import data.Table;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;
import locomotion.UnableToMoveException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PaletsX6 extends Script {
    private ArrayList<VectCartesian> positions;
    private boolean symetry;

    public PaletsX6(Master robot, Table table) {
        super(robot, table);
        /* on va faire plusieurs versions selon la combinaison de palets que l'on veut prendre et dans quel ordre
         *  (selon le côté de la table que l'on choisit ?)
         *  (selon si on est plus proche d'une extrémité ?)
         *  (selon si on s'est fait voler les palets ? (dans lequel pas on shift de côté))
         * */
        /*met en place les versions différentes*/
        versions = new ArrayList<>();
        versions.add(0);
        versions.add(1);
        versions.add(2);
        versions.add(3);
        /*position des 6 palets ( position dans le tableau positionS ) */
        this.positions = new ArrayList<>();
    }

    @Override
    public void execute(Integer version) {
            /*donne le côté duquel on commence à prendre les palets selon la position au début du script du robot.
     Autrement dit on divise la demi table en deux et selon cela on choisit de commencer à droite ou à gauche du distributeur
     */
            int i=0;
            //Position pour le côté droit
            //difference de ~100  entre chaque palet
            if (version == 0) { //rouge droite
            // version pour juste les rouges
                positions.add(new VectCartesian(905, 1206));
                positions.add(new VectCartesian(805 , 1206));
                positions.add(new VectCartesian(597, 1206));
            } else if (version == 1) {  // version pour juste les verts
             positions.add(new VectCartesian(905, 1206));
                positions.add(new VectCartesian(505, 1206));
            } else if (version == 2) {  //// version pour juste le bleu
                positions.add(new VectCartesian(834, 1206));
            }//version pour prendre les palets à la suite sauf le bleu
            else if (version ==3){
                positions.add(new VectCartesian(1000, 1196));
                positions.add(new VectCartesian(900, 1196));
                positions.add(new VectCartesian(800 , 1196));
                positions.add(new VectCartesian(700, 1196));
                positions.add(new VectCartesian(500, 1196));
            }
        try {
            //
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(symetry) {
                robot.turn(0);
                robot.computeNewPositionAndOrientation(Sick.UPPER_LEFT_CORNER_TOWARDS_0);
            } else {
                robot.turn(Math.PI);
                robot.computeNewPositionAndOrientation(Sick.UPPER_RIGHT_CORNER_TOWARDS_PI);
            }
            //Verifier les ascenseurs ?
            if(robot.getNbPaletsDroits()==0){
                //robot.useActuator(ActuatorsOrder.);
            }

            for (Vec2 position : positions) {

                robot.followPathTo(position,() -> this.executeWhileMovingToEntry(version));
                if(robot.getNbPaletsDroits()==5)
                {
                    robot.turn(0);
                    // on suppose que l'ascenseur est monté au mx au début
                    robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR,true);
                    robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR,true);
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE,true);


                }
                else {
                    robot.turn(Math.PI);
                    // on suppose que l'ascenseur est monté au mx au début
                    robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR,true);
                    robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR,true  );
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);

                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(version == 0) {
                    // TODO
                    robot.pushPaletDroit(CouleurPalet.ROUGE);
                    }
                else if(version == 1) {
                    // TODO
                    robot.pushPaletDroit(CouleurPalet.VERT);
                }
                else if(version ==2){
                    robot.pushPaletDroit(CouleurPalet.BLEU);
                }
                else if(version == 3){//Pour chaque palet
                    switch (i) {
                        case 0:robot.pushPaletDroit(CouleurPalet.ROUGE);
                                i++;
                            break;
                        case 1:robot.pushPaletDroit(CouleurPalet.VERT);
                            i++;
                            break;
                        case 2:robot.pushPaletDroit(CouleurPalet.ROUGE);
                            i++;
                            break;
                        case 3:robot.pushPaletDroit(CouleurPalet.ROUGE);
                            i++;
                            break;
                        case 4:robot.pushPaletDroit(CouleurPalet.VERT);
                            i++;
                            break;
                    }
                }
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
            // TODO
        }
    }
    @Override
    public Shape entryPosition(Integer version) {
        //position de départ directement au niveau du palet
        if (version == 0) {
            //Shape positionEntree = new Circle(new VectCartesian(1500-280,1206), 5);
            Shape positionEntree = new Circle(new VectCartesian(1000,1206), 5);
            return positionEntree;
        }
        else if (version == 1) {
            Shape positionEntree = new Circle(new VectCartesian(905,1206), 5);
            return positionEntree;
        }
        else if (version == 2) {
            Shape positionEntree = new Circle(new VectCartesian(834,1206), 5);
            return positionEntree;
        }
        else if (version == 3) {
            Shape positionEntree = new Circle(new VectCartesian(1500-191-65+20,450+600), 5);
            return positionEntree;
        }
        return null;
    }

    @Override
    public void executeWhileMovingToEntry(int version) {
        if(robot.getNbPaletsDroits()>=1){
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
        }
    }

    @Override
    public void finalize(Exception e) {

        // TODO: push palet
    }
    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        symetry = config.getString(ConfigData.COULEUR).equals("violet");
    }
}