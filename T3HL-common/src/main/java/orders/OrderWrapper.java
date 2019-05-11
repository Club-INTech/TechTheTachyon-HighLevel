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
import data.controlers.DataControler;
import orders.order.*;
import pfg.config.Config;
import orders.hooks.HookNames;
import utils.ConfigData;
import utils.Container;
import utils.Log;
import utils.communication.CommunicationException;
import utils.container.ContainerException;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;

import java.util.Locale;

/**
 * Classe qui permet d'envoyer tous les ordres
 *
 * @author yousra
 */
public class OrderWrapper implements Service {

    /**
     * Symétrie
     */
    private boolean symetry;

    /**
     * Simulation active
     */
    private boolean simulation;

    /**
     * On utitise comme connexion par défaut le bas niveau
     */
    private Connection llConnection;

    /**
     * Si on utlise la balise
     */
    private Boolean useBalise_Image;
    private Container container;
    /**
     * Le service de symétrie des ordres
     */
    private SymmetrizedActuatorOrderMap symmetrizedActuatorOrderMap;

    /**
     * Construit l'order wrapper
     * @param symmetrizedActuatorOrderMap
     *              service permettant de gérer la symétrie des ordres
     */
    private OrderWrapper(Container container, SymmetrizedActuatorOrderMap symmetrizedActuatorOrderMap) {
        this.container = container;
        this.symmetrizedActuatorOrderMap = symmetrizedActuatorOrderMap;
    }

    /**
     * Permet d'envoyer un ordre au bas niveau
     * @param order ordre quelconque
     */
    public void useActuator(Order order) {
        useActuator(order, false);
    }

    /**
     * Permet d'envoyer un ordre au bas niveau
     * @param order ordre quelconque
     * @param waitForConfirmation wait for confirmation of order
     */
    public void useActuator(Order order, boolean waitForConfirmation) {
        Order symetrisedOrder;
        if(symetry && order instanceof ActuatorsOrder) {
            symetrisedOrder=this.symmetrizedActuatorOrderMap.getSymmetrizedActuatorOrder((ActuatorsOrder) order);
            if(symetrisedOrder != null){
                order=symetrisedOrder;
            }

        }
        if(waitForConfirmation) {
            Log.COMMUNICATION.debug("Asking for confirmation for "+order.getOrderStr());
            this.sendString("!"+order.getOrderStr());

            // il y a une procédure de traitement des mouvements de bras qui n'est pas bloquante pour le LL,
            // il faut donc un système comme les ascenseurs pour attendre qu'ils aient fini bougent
            if(order instanceof ActuatorsOrder && ((ActuatorsOrder) order).isArmOrder()) {
                // quel côté bouge?
                String side = order.getOrderStr().split(" ")[1];

                // l'ordre est déjà symétrisé quand on récupère la position, c'est donc le bras (droit par exemple) physique, pas logique
                SensorState<Boolean> armMoving = SensorState.getArmMovingState(side);
                armMoving.setData(true);
                Log.COMMUNICATION.debug("Attente du bras du à l'ordre "+order.getOrderStr());

                // on attend que le bras ait fini
                waitWhileTrue(armMoving::getData);
            } else {
                SensorState.ACTUATOR_ACTUATING.setData(true);
                waitWhileTrue(SensorState.ACTUATOR_ACTUATING::getData);
                Log.COMMUNICATION.debug("Confirmation received for "+order.getOrderStr());
            }
            if(order instanceof ActuatorsOrder) {
                long duration = ((ActuatorsOrder) order).getActionDuration();
                if(duration > 0) {
                    try {
                        Thread.sleep(duration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            this.sendString(order.getOrderStr());
        }
    }

    /**
     * On envoit au bas niveau comme ordre d'avancer d'une certaine distance
     * @param distance distance dont on avance
     */
    public void moveLenghtwise(double distance, Runnable... parallelActions) {
        int d = (int) Math.round(distance);
        this.sendString(String.format(Locale.US, "%s %d", MotionOrder.MOVE_LENTGHWISE.getOrderStr(), d));
        runAll(parallelActions);
    }

    /**
     * On envoit au bas niveau comme ordre de tourner
     * @param angle  angle avec lequel on veut tourner
     */
    public void turn(double angle, Runnable... parallelActions) {
        if(symetry) {
            angle=(Math.PI - angle)%(2*Math.PI);
        }
        this.sendString(String.format(Locale.US, "%s %.5f", MotionOrder.TURN.getOrderStr(), angle));
        runAll(parallelActions);
    }

    /**
     * On envoit au LL l'ordre d'aller en ligne droite jusqu'à un point
     * @param point point visé
     */
    public void moveToPoint(Vec2 point, Runnable... parallelActions) {
        Vec2 p = point;
        if(symetry) {
            p = p.symetrizeVector();
        }
        this.sendString(String.format(Locale.US, "%s %d %d", MotionOrder.MOVE_TO_POINT.getOrderStr(), p.getX(), p.getY()));
        runAll(parallelActions);
    }

    /**
     * On envoit au bas niveau comme ordre de s'arrêter
     */
    public void immobilise() {
        this.sendString(MotionOrder.STOP.getOrderStr());
    }

    /**
     * On dit au bas niveau la vitesse de translation qu'on veut
     * @param speed la vitesse qu'on veut
     */
    public void setTranslationnalSpeed(float speed) {
        this.sendString(String.format(Locale.US, "%s %.5f", SpeedOrder.SET_TRANSLATION_SPEED.getOrderStr(), speed));
    }

    /**
     * On dit au abs niveau la vitesse de rotation qu'on veut
     * @param rotationSpeed la vitesse de rotation qu'on veut
     */
    public void setRotationnalSpeed(double rotationSpeed) {
        this.sendString(String.format(Locale.US, "%s %.5f", SpeedOrder.SET_ROTATIONNAL_SPEED.getOrderStr(), (float) rotationSpeed));
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
        if(symetry){
            x=-x;
            orientation= Calculs.modulo(Math.PI - orientation, Math.PI);
        }
        // cette demande nécessite une synchro
        if(synchronize) {
            SensorState.ACTUATOR_ACTUATING.setData(true);
            this.sendString(String.format(Locale.US, "!%s %d %d %.5f",
                    PositionAndOrientationOrder.SET_POSITION_AND_ORIENTATION.getOrderStr(), x,y, orientation));
            waitWhileTrue(SensorState.ACTUATOR_ACTUATING::getData);
            try {
                container.getService(DataControler.class).waitForTwoPositionUpdates();
            } catch (ContainerException e) {
                e.printStackTrace();
            }
        } else {
            this.sendString(String.format(Locale.US, "%s %d %d %.5f",
                    PositionAndOrientationOrder.SET_POSITION_AND_ORIENTATION.getOrderStr(), x,y, orientation));
        }
    }

    /**
     * On dit au bas niveau quelle orientation le robot a
     * @param orientation orientation du robot
     */
    public void setOrientation(double orientation) {
        if(symetry) {
            orientation=(Math.PI - orientation)%(2*Math.PI);
        }
        this.sendString(String.format(Locale.US, "%s %.5f", PositionAndOrientationOrder.SET_ORIENTATION.getOrderStr(), orientation));
    }

    /**
     * Envoyer l'order de récupérer les données des sicks
     */
    public void getSickData() {
        this.sendString(String.format(Locale.US, "%s", PositionAndOrientationOrder.DATA_SICK.getOrderStr()));
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
        if(symetry){
            posTrigger = posTrigger.symetrizeVector();
            Log.HOOK.debug("la position envoyée au bas niveau pour le hook"+posTrigger.toString());
            orientation=(Math.PI - orientation)%(2*Math.PI);
            Log.HOOK.debug("l'orientation envoyée au bas niveau pour le hook"+orientation);
            if( order instanceof ActuatorsOrder) {
                symetrisedOrder = symmetrizedActuatorOrderMap.getSymmetrizedActuatorOrder((ActuatorsOrder) order);
                if(symetrisedOrder !=null){
                    order=symetrisedOrder;
                }
            }

        }
        this.sendString(String.format(Locale.US, "%s %d %s %d %.5f %.5f %s",
                HooksOrder.INITIALISE_HOOK.getOrderStr(), id, posTrigger.toStringEth(), tolerency, orientation, tolerencyAngle, order.getOrderStr()));
    }

    /**
     * Envoie l'ordre moveToPoint (goto) au LL (ne prend pas en compte les obstacles)
     * @param point point auquel le LL doit se rendre
     */
    public void gotoPoint(Vec2 point) {
        if(symetry) {
            point.symetrize();
        }
        this.sendString(String.format(Locale.US, "%s %d %d", MotionOrder.MOVE_TO_POINT.getOrderStr(), point.getX(), point.getY()));
    }

    /**
     * Active un hook
     * @param hook hook à activer
     */
    public void enableHook(HookNames hook) {
        this.sendString(String.format(Locale.US, "%s %d", HooksOrder.ENABLE_HOOK.getOrderStr(), hook.getId()));
    }

    /**
     * Desactive un hook
     * @param hook
     */
    public void disableHook(HookNames hook) {
        this.sendString(String.format(Locale.US, "%s %d", HooksOrder.DISABLE_HOOK.getOrderStr(), hook.getId()));
    }

    /**
     * Envoie un message au ll et traite les potentielles erreurs
     * @param message
     *              message à envoyer tel quel
     */
    public void sendString(String message) {
        try {
            llConnection.send(message);
            Log.ORDERS.debug("Sent to LL: "+message);
        } catch (CommunicationException e) {
            e.printStackTrace();
            try {
                while (!llConnection.isInitiated()) {
                    Thread.sleep(5);
                }
                llConnection.send(message);
                Log.ORDERS.debug("Sent to LL: "+message);
            } catch (CommunicationException | InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Exécute toutes les actions du tableau donné
     * @param actions les actions à exécuter
     */
    private void runAll(Runnable[] actions) {
        for (Runnable action : actions) {
            action.run();
        }
    }

    /**
     * Envoie un ping au LL pour vérifier que la connexion est encore active
     */
    public void ping() {
        sendString(MiscOrder.PING.getOrderStr());
    }

    @Override
    public void updateConfig(Config config) {
        // On est du côté violet par défaut , le HL pense en violet
        symetry = config.getString(ConfigData.COULEUR).equals("violet");
        this.simulation = config.getBoolean(ConfigData.SIMULATION);
        useBalise_Image = config.getBoolean(ConfigData.USING_BALISE_IMAGE);

        boolean isMaster = config.getBoolean(ConfigData.MASTER);
        if (this.simulation) {
            this.llConnection = Connection.MASTER_LL_SIMULATEUR;
        } else {
            if(isMaster) {
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
        Log.STRATEGY.debug("Waiting jumper...");
        SensorState.WAITING_JUMPER.setData(true);
        sendString("waitJumper");
        waitWhileTrue(SensorState.WAITING_JUMPER::getData);
        if(useBalise_Image) {
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
}
