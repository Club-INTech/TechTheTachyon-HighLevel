package scripts;

import orders.order.ActuatorsOrder;
import data.Table;
import pfg.config.Config;
import robot.Slave;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;

public class Cracheur extends Script {
    /**
     * Position d'entrée du script
     */

    //Valeurs à ajuster pour le robot secondaire
    private int xEntry = 130;
    private int yEntry = 1580;

    private int nbPalets = robot.getNbPaletsDroits();


    public Cracheur(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {

        for (int i = 1; i < nbPalets; i++) {
            robot.useActuator(ActuatorsOrder.CRACHE_UN_PALET);
            robot.useActuator(ActuatorsOrder.RANGE_CRACHE_PALET);
            robot.useActuator(ActuatorsOrder.MONTE_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
            robot.popPaletDroit();
        }
    }
    @Override //à adapter
    public Shape entryPosition(Integer version) { return new Circle(new VectCartesian(xEntry, yEntry), 5); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { }
}
