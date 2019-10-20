/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.
 *
 * INTech's HighLevel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * INTech's HighLevel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with it.  If not, see <http://www.gnu.org/licenses/>.
 **/

package orders;

import connection.Connection;
import data.SensorState;
import data.controlers.DataController;
import lowlevel.order.ElevatorOrder;
import lowlevel.order.Order;
import lowlevel.order.ServoGroupOrder;
import lowlevel.order.SidedOrder;
import orders.hooks.HookNames;
import orders.order.*;
import pfg.config.Config;
import pfg.config.Configurable;
import utils.ConfigData;
import utils.HLInstance;
import utils.Log;
import utils.RobotSide;
import utils.communication.CommunicationException;
import utils.container.ContainerException;
import utils.container.Module;
import utils.math.Calculs;
import utils.math.Vec2;

/**
 * Classe qui permet d'envoyer tous les ordres
 *
 * @author yousra
 */
public class OrderWrapper implements Module {

    /**
     * Symétrie
     */
    @Configurable
    private boolean symetry;

    /**
     * Simulation active
     */
    @Configurable
    private boolean simulation;

    /**
     * On utitise comme connexion par défaut le bas niveau
     */
    private Connection llConnection;

    /**
     * Si on utlise la balise
     */
    @Configurable
    private boolean usingBaliseImage;
    private HLInstance hl;
    private boolean forceInversion;

    /**
     * Construit l'order wrapper
     * @param symmetrizedActuatorOrderMap
     *              service permettant de gérer la symétrie des ordres
     */
    private OrderWrapper(HLInstance hl) {
        this.hl = hl;
    }

    /**
     * On envoit au bas niveau comme ordre d'avancer d'une certaine distance
     * @param distance distance dont on avance
     */
    public void moveLenghtwise(double distance, boolean expectedWallImpact, Runnable... parallelActions) {
        int d = (int) Math.round(distance);
        sendString(MotionOrders.MoveLengthwise.with(d, expectedWallImpact));
        runAll(parallelActions);
    }

    /**
     * On envoit au bas niveau comme ordre de tourner
     * @param angle  angle avec lequel on veut tourner
     */
    public void turn(double angle, Runnable... parallelActions) {
        if(symetry) { // pas shouldSymetrize parce qu'il faut rester au bon endroit sur la table
            angle=(Math.PI - angle)%(2*Math.PI);
        }
        this.sendString(MotionOrders.Turn.with(angle));
        runAll(parallelActions);
    }

    /**
     * On envoit au LL l'ordre d'aller en ligne droite jusqu'à un point
     * @param point point visé
     */
    public void moveToPoint(Vec2 point, Runnable... parallelActions) {
        Vec2 p = point;
        if(symetry) { // pas shouldSymetrize parce qu'il faut rester au bon endroit sur la table
            p = p.symetrizeVector();
        }
        this.sendString(MotionOrders.MoveToPoint.with(p.getX(), p.getY()));
        runAll(parallelActions);
    }

    /**
     * On envoit au bas niveau comme ordre de s'arrêter
     */
    public void immobilise() {
        perform(MotionOrders.Stop);
        SensorState.MOVING.setData(false);
    }


    /**
     * Couper l'asserv en rotation
     */
    public void noRotationControl() {this.sendString(MotionOrders.DisableRotationControl.toLL());}

    /**
     * Activer l'asserv en rotation
     */
    public void rotationControl() {this.sendString(MotionOrders.EnableRotationControl.toLL());}

    /**
     * On dit au bas niveau la vitesse de translation qu'on veut
     * @param speed la vitesse qu'on veut
     */
    public void setTranslationnalSpeed(float speed) {
        this.sendString(SpeedOrders.SetTranslationSpeed.with(speed));
    }

    /**
     * On dit au abs niveau la vitesse de rotation qu'on veut
     * @param rotationSpeed la vitesse de rotation qu'on veut
     */
    public void setRotationnalSpeed(double rotationSpeed) {
        this.sendString(SpeedOrders.SetRotationalSpeed.with((float) rotationSpeed));
    }

    /**
     * Modifie les vitesses de translation et de rotation du robot
     * @param speed enum qui contient les deux vitesses
     */
    public void setBothSpeed(Speed speed) {
        this.setTranslationnalSpeed(speed.getTranslationSpeed());
        this.setRotationnalSpeed(speed.getRotationSpeed());
    }

    /**
     * On dit au bas niveau dans quelle position on est et quelle orientation on adopte
     * @param pos position du robot
     * @param orientation orientation du robot
     */
    public void setPositionAndOrientation(Vec2 pos, double orientation) {
        setPositionAndOrientation(pos, orientation, true);
    }

    public void setPositionAndOrientation(Vec2 pos, double orientation, boolean synchronize) {
        int x=pos.getX();
        int y=pos.getY();
        if(symetry) { // pas shouldSymetrize parce qu'il faut rester au bon endroit sur la table
            x=-x;
            orientation= Calculs.modulo(Math.PI - orientation, Math.PI);
        }
        // cette demande nécessite une synchro
        if(synchronize) {
            SensorState.ACTUATOR_ACTUATING.setData(true);
            this.sendWithConfirmation(PositionAndOrientationOrders.SetPositionAndOrientation.with(x, y, orientation));
            Module.waitWhileTrue(SensorState.ACTUATOR_ACTUATING::getData);
            try {
                hl.module(DataController.class).waitForTwoPositionUpdates();
            } catch (ContainerException e) {
                e.printStackTrace();
            }
        } else {
            this.sendString(PositionAndOrientationOrders.SetPositionAndOrientation.with(x, y, orientation));
        }
    }

    /**
     * On dit au bas niveau quelle orientation le robot a
     * @param orientation orientation du robot
     */
    public void setOrientation(double orientation) {
        if(symetry) { // pas shouldSymetrize parce qu'il faut rester au bon endroit sur la table
            orientation=(Math.PI - orientation)%(2*Math.PI);
        }
        this.sendString(PositionAndOrientationOrders.SetOrientation.with(orientation));
    }

    /**
     * Envoyer l'order de récupérer les données des sicks
     */
    public void getSickData() {
        this.sendString(PositionAndOrientationOrders.AskSickData.toLL());
    }

    /**
     * Permet de configurer un hook
     * @param id id du hook
     * @param posTrigger position où on active le hook
     * @param tolerency tolérance qu'on veut sur la position
     * @param orientation l'orientation du robot où on active le hook
     * @param tolerencyAngle l'angle de tolérance sur l'orientation
     * @param order l'ordre à exécuter pendant que le robot bouge
     */
    public void configureHook(int id, Vec2 posTrigger, int tolerency, double orientation, double tolerencyAngle, Order order) {
        Order symetrisedOrder;
        if(symetry) { // pas shouldSymetrize parce qu'il faut rester au bon endroit sur la table
            posTrigger = posTrigger.symetrizeVector();
            Log.HOOK.debug("la position envoyée au bas niveau pour le hook"+posTrigger.toString());
            orientation=(Math.PI - orientation)%(2*Math.PI);
            Log.HOOK.debug("l'orientation envoyée au bas niveau pour le hook"+orientation);
            if( order instanceof SidedOrder) {
                symetrisedOrder = ((SidedOrder) order).symetrize();
                if(symetrisedOrder !=null){
                    order=symetrisedOrder;
                }
            }

        }
        this.sendString(HookOrders.InitialiseHook.with(id, posTrigger.toStringEth(), tolerency, orientation, tolerencyAngle, order.toLL()));
    }

    /**
     * Envoie l'ordre moveToPoint (goto) au LL (ne prend pas en compte les obstacles)
     * @param point point auquel le LL doit se rendre
     */
    public void gotoPoint(Vec2 point) {
        if(symetry) { // pas shouldSymetrize parce qu'il faut rester au bon endroit sur la table
            point.symetrize();
        }
        this.sendString(MotionOrders.MoveToPoint.with(point.getX(), point.getY()));
    }

    /**
     * Active un hook
     * @param hook hook à activer
     */
    public void enableHook(HookNames hook) {
        this.sendString(HookOrders.EnableHook.with(hook.getId()));
    }

    /**
     * Desactive un hook
     * @param hook
     */
    public void disableHook(HookNames hook) {
        this.sendString(HookOrders.DisableHook.with(hook.getId()));
    }

    /**
     * Envoie un message au ll et traite les potentielles erreurs
     * @param message
     *              message à envoyer tel quel
     */
    public void sendString(String message) {
        try {
            llConnection.send(message);
            Log.ORDERS.debug("Sent to LL: "+message, 3);
        } catch (CommunicationException e) {
            e.printStackTrace();
            try {
                while (!llConnection.isInitiated()) {
                    Thread.sleep(5);
                }
                llConnection.send(message);
                Log.ORDERS.debug("Sent to LL: "+message, 3);
            } catch (CommunicationException | InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Envoie un message au LL et demande une confirmation de l'ordre
     * @param message
     */
    public void sendWithConfirmation(String message) {
        this.sendString("!"+message);
    }

    /**
     * Exécute toutes les actions du tableau donné
     * @param actions les actions à exécuter
     */
    private void runAll(Runnable[] actions) {
        if(actions == null)
            return;
        for (Runnable action : actions) {
            action.run();
        }
    }

    /**
     * Envoie un ping au LL pour vérifier que la connexion est encore active
     */
    public void ping() {
        sendString(MiscOrders.Ping.toLL());
    }

    @Override
    public void updateConfig(Config config) {
        boolean master = config.get(ConfigData.MASTER);
        if (this.simulation) {
            this.llConnection = Connection.MASTER_LL_SIMULATEUR;
        } else {
            if(master) {
                this.llConnection = Connection.TEENSY_MASTER;
            } else {
                this.llConnection = Connection.TEENSY_SLAVE;
            }
        }
    }

    /**
     * On set la connection, c'est pour faire les tests en local, faire très attention quand on utilise cette méthode
     * @param connection : à qui on veut envoyer des ordres
     */
    public void setConnection(Connection connection) {
        this.llConnection = connection;
    }

    public void waitJumper() {
        if(simulation)
            return;
        Log.STRATEGY.debug("Waiting jumper...");
        SensorState.WAITING_JUMPER.setData(true);
        sendString("waitJumper");
        Module.waitWhileTrue(SensorState.WAITING_JUMPER::getData);
        if(usingBaliseImage) {
            try {
                Connection.BALISE_IMAGE.send("GO");
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
        }
        Log.STRATEGY.debug("GOGOGO!!!");
    }

    public void endMatch() {
        Log.STRATEGY.debug("Fin du match!");
        sendString("endMatch");
    }

    public boolean shouldSymetrize() {
        return symetry ^ forceInversion;
    }

    /**
     * <b>A NE PAS APPELER SANS SAVOIR CE QU'ON FAIT</b><hr/>
     * Permet de (re-)forcer une symétrie des ordres qu'on envoie quand on veut pas coder les mêmes pour deux côtés du robot
     */
    public void setInverted(boolean inverted) {
        this.forceInversion = inverted;
    }

    public void forceStop() {
        perform(MotionOrders.ForceStop);
    }

    /**
     * Utilise un ordre donné, sans attendre de confirmation
     * @param order l'ordre à exécuter
     */
    public void perform(lowlevel.order.Order order) {
        perform(order, false);
    }

    /**
     * Utilise un ordre donné, avec possibilité d'attendre la confirmation du LL
     * @param order l'ordre à exécuter
     * @param waitForConfirmation doit-on attendre la confirmation du LL?
     */
    public void perform(lowlevel.order.Order order, boolean waitForConfirmation) {
        if(order instanceof SidedOrder && shouldSymetrize()) {
            order = ((SidedOrder) order).symetrize();
        }
        if(waitForConfirmation) {
            Log.COMMUNICATION.debug("Asking for confirmation for "+order.toLL());
            if (order instanceof ServoGroupOrder) {
                RobotSide side = RobotSide.RIGHT;
                if(order instanceof SidedOrder) { // TODO: unsided versions?
                    side = ((SidedOrder) order).side();
                }
                SensorState<Boolean> state = SensorState.getArmMovingState(side.name().toLowerCase());
                state.setData(true);
                this.sendString("!"+order.toLL());
                Module.waitWhileTrue(state);
            } else if(order instanceof ElevatorOrder && order instanceof SidedOrder) {
                RobotSide side = RobotSide.RIGHT;
                if(order instanceof SidedOrder) { // TODO: unsided versions?
                    side = ((SidedOrder) order).side();
                }
                SensorState<Boolean> state = SensorState.RIGHT_ELEVATOR_MOVING;
                if(side == RobotSide.LEFT) {
                    state = SensorState.LEFT_ELEVATOR_MOVING;
                }
                state.setData(true);
                this.sendString("!"+order.toLL());
                Module.waitWhileTrue(state);
            } else {
                SensorState.ACTUATOR_ACTUATING.setData(true);
                this.sendString("!"+order.toLL());
                Module.waitWhileTrue(SensorState.ACTUATOR_ACTUATING);
            }
        } else {
            sendString(order.toLL());
        }
    }
}
