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
    private int xEntry = 2000;
    private int yEntry = 2000;

    private int nbPalets = 3;


    public Cracheur(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        //situation de départ avec 3 palets dans l'ascenseur
        for (int j = 1; j<= nbPalets; j++) {
            robot.useActuator(ActuatorsOrder.MONTE_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
            robot.useActuator(ActuatorsOrder.CRACHE_UN_PALET);
        }
    }
    @Override //à adapter
    public Shape entryPosition(Integer version) { return new Circle(new VectCartesian(xEntry, yEntry), 1000); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { }
}
