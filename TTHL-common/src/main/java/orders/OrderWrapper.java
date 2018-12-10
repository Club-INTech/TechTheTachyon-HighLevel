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
import orders.order.MotionOrder;
import orders.order.ActuatorsOrder;
import orders.order.Order;
import orders.order.SpeedOrder;
import orders.order.PositionAndOrientationOrder;
import orders.order.HooksOrder;
import pfg.config.Config;
import orders.hooks.HookNames;
import utils.ConfigData;
import utils.Log;
import utils.communication.CommunicationException;
import utils.container.Service;
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
     * On utitise comme connexion par défaut le bas niveau
     */
    private Connection llConnection = Connection.TEENSY_MASTER;

    /**
     * Le service de symétrie des ordres
     */
    private SymmetrizedActuatorOrderMap symmetrizedActuatorOrderMap;

    /**
     * Construit l'order wrapper
     * @param symmetrizedActuatorOrderMap
     *              service permettant de gérer la symétrie des ordres
     */
    private OrderWrapper(SymmetrizedActuatorOrderMap symmetrizedActuatorOrderMap){
        this.symmetrizedActuatorOrderMap = symmetrizedActuatorOrderMap;
    }

    /**
     * Permet d'envoyer un ordre au bas niveau
     * @param order ordre quelconque
     */
    public void useActuator(Order order)
    {
        Order symetrisedOrder;
        if(symetry && order instanceof ActuatorsOrder){
            symetrisedOrder=this.symmetrizedActuatorOrderMap.getSymmetrizedActuatorOrder((ActuatorsOrder) order);
            if(symetrisedOrder != null){
                order=symetrisedOrder;
            }
        }
        try {
            llConnection.send(order.getOrderStr());
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * On envoit au bas niveau comme ordre d'avancer d'une certaine distance
     * @param distance distance dont on avance
     */
    public void moveLenghtwise(double distance){
        int d = (int)Math.round(distance);
        try {
            llConnection.send(String.format(Locale.US, "%s %d", MotionOrder.MOVE_LENTGHWISE.getOrderStr(), d));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * On envoit au bas niveau comme ordre de tourner
     * @param angle  angle aveclequel on veut tourner
     */
    public void turn(double angle) {
        if(symetry) {
            angle=(Math.PI - angle)%(2*Math.PI);
        }
        try {
            llConnection.send(String.format(Locale.US, "%s %.3f", MotionOrder.TURN.getOrderStr(), angle));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * On envoit au LL l'ordre d'aller en ligne droite jusqu'à un point
     * @param point point visé
     */
    public void moveToPoint(Vec2 point) {
        Vec2 p = point;
        if(symetry) {
            p.symetrize();
        }
        try {
            llConnection.send(String.format(Locale.US, "%s %d %d", MotionOrder.MOVE_TO_POINT.getOrderStr(), point.getX(), point.getY()));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * On envoit au bas niveau comme ordre de s'arrêter
     */
    public void immobilise() {
        try {
            llConnection.send(MotionOrder.STOP.getOrderStr());
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * On dit au bas niveau la vitesse de translation qu'on veut
     * @param speed la vitesse qu'on veut
     */
    public void setTranslationnalSpeed(float speed) {
        try {
            llConnection.send(String.format(Locale.US, "%s %.3f", SpeedOrder.SET_TRANSLATION_SPEED.getOrderStr(), speed));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * On dit au abs niveau la vitesse de rotation qu'on veut
     * @param rotationSpeed la vitesse de rotation qu'on veut
     */
    public void setRotationnalSpeed(double rotationSpeed) {
        try {
            llConnection.send(String.format(Locale.US, "%s %.3f", SpeedOrder.SET_ROTATIONNAL_SPEED.getOrderStr(), (float) rotationSpeed));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifie les vitesses de translation et de rotation du robot
     * @param speed enum qui contient les deux vitesses
     */
    public void setBothSpeed(Speed speed){
        this.setTranslationnalSpeed(speed.getTranslationSpeed());
        this.setRotationnalSpeed(speed.getRotationSpeed());
    }

    /**
     * On dit au bas niveau dans quelle position on est et quelle orientation on adopte
     * @param pos position du robot
     * @param orientation orientation du robot
     */
    public void setPositionAndOrientation(Vec2 pos, double orientation)
    {
        int x=pos.getX();
        int y=pos.getY();
        if(symetry){
            x=-x;
            orientation=(Math.PI - orientation)%(2*Math.PI);
        }
        try {
            llConnection.send(String.format(Locale.US, "%s %d %d %.3f",
                    PositionAndOrientationOrder.SET_POSITION_AND_ORIENTATION.getOrderStr(), x,y, orientation));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * On dit au bas niveau quelle orientation le robot a
     * @param orientation orientation du robot
     */
    public void setOrientation(double orientation)
    {
        if(symetry){
            orientation=(Math.PI - orientation)%(2*Math.PI);
        }
        try {
            llConnection.send(String.format(Locale.US, "%s %.3f", PositionAndOrientationOrder.SET_ORIENTATION.getOrderStr(), orientation));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
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
    public void configureHook(int id, Vec2 posTrigger, int tolerency, double orientation, double tolerencyAngle, Order order){
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
        try {
            llConnection.send(String.format(Locale.US, "%s %d %s %d %.3f %.3f %s",
                    HooksOrder.INITIALISE_HOOK.getOrderStr(), id, posTrigger.toStringEth(), tolerency, orientation, tolerencyAngle, order.getOrderStr()));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Active un hook
     * @param hook hook à activer
     */
    public void enableHook(HookNames hook) {
        try {
            llConnection.send(String.format(Locale.US, "%s %d", HooksOrder.ENABLE_HOOK.getOrderStr(), hook.getId()));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Desactive un hook
     * @param hook
     */
    public void disableHook(HookNames hook){
        try {
            llConnection.send(String.format(Locale.US, "%s %d", HooksOrder.DISABLE_HOOK.getOrderStr(), hook.getId()));
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateConfig(Config config) {
        //On est du côté violet par défaut , le HL pense en violet
        symetry=config.getString(ConfigData.COULEUR).equals("jaune");
    }

    /**
     * On set la connection, c'est pour faire les tests en local, faire très attention quand on utilise cette méthode
     * @param connection : à qui on veut envoyer des ordres
     */
    public void setConnection(Connection connection) {
        this.llConnection = connection;
    }
}
