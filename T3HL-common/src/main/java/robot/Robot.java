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

package robot;

import data.CouleurPalet;
import data.SensorState;
import data.Sick;
import data.XYO;
import locomotion.Locomotion;
import locomotion.UnableToMoveException;
import orders.OrderWrapper;
import orders.hooks.HookFactory;
import orders.hooks.HookNames;
import orders.order.ActuatorsOrder;
import orders.Speed;
import orders.order.MontlheryOrder;
import pfg.config.Config;
import utils.ConfigData;
import utils.RobotSide;
import utils.communication.SimulatorDebug;
import utils.container.Service;
import utils.math.Vec2;

import java.util.Objects;
import java.util.Stack;

/**
 * Classe regroupant tout les services et fonctionnalitées de base du robot
 * TODO : Compléter
 *
 * @author rem
 */
public abstract class Robot implements Service {

    /**
     * Permet d'envoyer des infos de debug
     */
    private SimulatorDebug simulatorDebug;
    public int score ;


    /**
     * Service qui permet au robot de bouger
     */
    protected Locomotion locomotion;

    /**
     * Service qui permet au robot d'envoyer des ordres au LL
     */
    protected OrderWrapper orderWrapper;

    /**
     * Service de gestion des hooks
     */
    protected HookFactory hookFactory;

    /**
     * Position et Orientation du robot
     */
    protected XYO xyo;
    private long LOOP_SLEEP_TIME;

    private Stack<CouleurPalet> leftElevator;
    private Stack<CouleurPalet> rightElevator;

    /**
     * @param locomotion
     *              service de mouvement du robot
     * @param orderWrapper
     *              service d'envoie d'ordre vers le LL
     */
    protected Robot(Locomotion locomotion, OrderWrapper orderWrapper, HookFactory hookFactory, SimulatorDebug simulatorDebug) {
        this.simulatorDebug = simulatorDebug;
        this.locomotion = locomotion;
        this.orderWrapper = orderWrapper;
        this.hookFactory = hookFactory;
    }


    public void increaseScore(int points){
        this.score = this.score + points;
    }

    public void waitForLeftElevator() {
        waitForElevator("left");
    }

    public void waitForRightElevator() {
        waitForElevator("right");
    }

    public void waitForElevator(String side) {
        SensorState state;
        switch (side.toLowerCase()) {
            case "left":
                state = SensorState.LEFT_ELEVATOR_MOVING;
                break;
            case "right":
                state = SensorState.RIGHT_ELEVATOR_MOVING;
                break;
            default:
                throw new IllegalArgumentException("Côté non reconnu: "+side);
        }
        state.setData(true);
        while((boolean)state.getData()) { // tant que l'ascenseur bouge
            try {
                Thread.sleep(LOOP_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ordonnes un 'goto' vers le LL. ATTENTION! Cette méthode ne prend PAS en compte le pathfinding! Si ça va dans le mur c'est votre faute
     * @param point le point vers lequel aller
     * @throws UnableToMoveException
     *              en cas de problème de blocage/adversaire
     */
    public void gotoPoint(Vec2 point) throws UnableToMoveException {
        this.locomotion.gotoPoint(point);
    }

    /**
     * Permet au robot d'aller jusqu'à un point donnée
     * @param point
     *              le point visé
     * @throws UnableToMoveException
     *              en cas de problème de blocage/adversaire
     */
    public void followPathTo(Vec2 point) throws UnableToMoveException {
        this.locomotion.followPathTo(point);
    }

    /**
     * Permet au robot d'avancer/recluer en ligne droite
     * @param distance
     *              la distance à parcourir, négative si l'on veut aller en arrière
     * @param expectedWallImpact
     *              true si l'on s'attend à un blocage mécanique (lorsque l'on veut se caler contre le mur par exemple)
     * @throws UnableToMoveException
     *              en cas de problèmes de blocage/adversaire
     */
    public void moveLengthwise(int distance, boolean expectedWallImpact) throws UnableToMoveException {
        this.locomotion.moveLenghtwise(distance, expectedWallImpact);
    }

    /**
     * Permet au robot de tourner sur lui-même
     * @param angle
     *              angle absolue vers lequel on veut se tourner
     * @throws UnableToMoveException
     *              en cas de problème de blocage/adversaire
     */
    public void turn(double angle) throws UnableToMoveException {
        this.locomotion.turn(angle);
    }

    /**
     * Permet au robot d'utiliser un actionneur
     * @param order
     *              l'ordre que l'on veut executer
     * @param waitForConfirmation
     *              Attendre une confirmation du LL avant de continuer?
     */
    public void useActuator(ActuatorsOrder order, boolean waitForConfirmation) {
        this.orderWrapper.useActuator(order, waitForConfirmation);
    }

    /**
     * Permet au robot d'utiliser un actionneur
     * @param order
     *              l'ordre que l'on veut executer
     */
    public void useActuator(ActuatorsOrder order) {
        this.orderWrapper.useActuator(order, false);
    }


    /**
     * Change la vitesse du LL
     * @param speed
     *              la vitesse souhaitée
     */
    public void setSpeed(Speed speed) {
        this.orderWrapper.setBothSpeed(speed);
    }

    /**
     * Change la position du LL
     * @param pos
     *              position souhaitée
     * @param orientation
     *              orientation souhaitée
     */
    public void setPositionAndOrientation(Vec2 pos, double orientation) {
        this.orderWrapper.setPositionAndOrientation(pos, orientation);
    }

    /**
     * Configure des hooks
     * @param hooks
     *              les hooks à configurer
     */
    public void configureHook(HookNames... hooks) {
        //TODO
    }

    /**
     * Méthode qui permet le recalage avec les sicks
     */
    public void computeNewPositionAndOrientation(){
        this.orderWrapper.getSickData();
        XYO newXYO = Sick.getNewXYO();

        // remplacement de la position dans le HL
        XYO.getRobotInstance().update(newXYO.getPosition().getX(), newXYO.getPosition().getY(), newXYO.getOrientation());

        // remplacement de la position dans le LL
        this.orderWrapper.setPositionAndOrientation(newXYO.getPosition(), newXYO.getOrientation());
    }

    /**
     * Actives le mode montlhery
     */
    public void switchToMontlheryMode() {
        this.orderWrapper.sendString(MontlheryOrder.MONTLHERY.getOrderStr());
        this.orderWrapper.sendString(MontlheryOrder.MAX_ROTATION_SPEED.getOrderStr()+" "+Math.PI/8f); // 1/4 de tour par seconde
        this.orderWrapper.sendString(MontlheryOrder.MAX_TRANSLATION_SPEED.getOrderStr()+" 90"); // 30 mm/s
    }

    // Gestion des ascenseurs

    /**
     * Renvoie le nombre de palets dans l'ascenseur de droite
     * @return
     */
    public int getNbPaletsDroits() {
        Objects.requireNonNull(rightElevator, "Tentative de compter le nombre de palets dans l'ascenseur de droite alors qu'il n'y a pas d'ascenseur à droite dans ce robot!");
        return rightElevator.size();
    }

    /**
     * Renvoie le nombre de palets dans l'ascenseur de droite
     * @return
     */
    public int getNbPaletsGauches() {
        Objects.requireNonNull(rightElevator, "Tentative de compter le nombre de palets dans l'ascenseur de droite alors qu'il n'y a pas d'ascenseur à droite dans ce robot!");
        return leftElevator.size();
    }

    // TODO: FIXME
    /**
     * Initialises l'ascenseur de droite
     */
    protected void createRightElevator() {
        this.rightElevator = new Stack<>();
    }

    /**
     * Initialises l'ascenseur de gauche
     */
    protected void createLeftElevator() {
        this.leftElevator = new Stack<>();
    }

    /**
     * Envoie une mise à jour de la liste de palets au simulateur si jamais il est connecté
     */
    private void sendElevatorUpdate() {
        if(leftElevator != null)
            simulatorDebug.sendElevatorContents(RobotSide.LEFT, leftElevator);
        if(rightElevator != null)
            simulatorDebug.sendElevatorContents(RobotSide.RIGHT, rightElevator);
    }

    /**
     * Ajoute un palet dans l'ascenseur de droite
     * @throws NullPointerException si l'ascenseur n'existe pas
     */
    public void pushPaletDroit(CouleurPalet palet) {
        //if (CouleurPalet.getCouleurPalRecu() != CouleurPalet.PAS_DE_PALET) {
        // ascenseurDroite.push(CouleurPalet.getCouleurPalRecu());
        //}
        Objects.requireNonNull(rightElevator, "Tentative d'insérer un palet dans l'ascenseur de droite alors qu'il n'y a pas d'ascenseur à droite dans ce robot!");
        rightElevator.push(palet);
        sendElevatorUpdate();
    }

    /**
     * Ajoute un palet dans l'ascenseur de gauche
     * @throws NullPointerException si l'ascenseur n'existe pas
     */
    public void pushPaletGauche(CouleurPalet palet) {
        // if (CouleurPalet.getCouleurPalRecu() != CouleurPalet.PAS_DE_PALET) {
        //ascenseurGauche.push(CouleurPalet.getCouleurPalRecu());
        //}
        Objects.requireNonNull(leftElevator, "Tentative d'insérer un palet dans l'ascenseur de gauche alors qu'il n'y a pas d'ascenseur à droite dans ce robot!");
        leftElevator.push(palet);
        sendElevatorUpdate();
    }

    /**
     * Retire un palet dans l'ascenseur de droite
     * @throws NullPointerException si l'ascenseur n'existe pas
     */
    public CouleurPalet popPaletDroit() {
        Objects.requireNonNull(rightElevator, "Tentative de retirer un palet dans l'ascenseur de droite alors qu'il n'y a pas d'ascenseur à droite dans ce robot!");
        CouleurPalet result = rightElevator.pop();
        sendElevatorUpdate();
        return result;
    }

    /**
     * Retire un palet dans l'ascenseur de gauche
     * @throws NullPointerException si l'ascenseur n'existe pas
     */
    public CouleurPalet popPaletGauche() {
        Objects.requireNonNull(leftElevator, "Tentative de retirer un palet dans l'ascenseur de gauche alors qu'il n'y a pas d'ascenseur à droite dans ce robot!");
        CouleurPalet result = leftElevator.pop();
        sendElevatorUpdate();
        return result;
    }

    /**
     * Renvoies l'ascenseur de gauche, ou 'null' s'il n'existe pas
     */
    public Stack<CouleurPalet> getLeftElevatorOrNull() {
        return leftElevator;
    }

    /**
     * Renvoies l'ascenseur de droite, ou 'null' s'il n'existe pas
     */
    public Stack<CouleurPalet> getRightElevatorOrNull() {
        return rightElevator;
    }

    public XYO getXyo() { return this.xyo;}

    @Override
    public void updateConfig(Config config) {
        LOOP_SLEEP_TIME = config.getLong(ConfigData.LOCOMOTION_LOOP_DELAY);
    }
}
