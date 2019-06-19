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

package connection;

import pfg.config.Config;
import pfg.config.ConfigInfo;
import utils.ConfigData;
import utils.Log;
import utils.communication.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Defines all the communication interfaces
 *
 * @author william, rem
 */
public enum Connection {
    MASTER(ConfigData.MASTER_IP, ConfigData.MASTER_PORT, SocketClientInterface.class, false),
    SLAVE(ConfigData.LOCALHOST, ConfigData.MASTER_PORT, SocketServerInterface.class, false),
    LIDAR_DATA(ConfigData.LOCALHOST, ConfigData.LIDAR_DATA_PORT, SocketClientInterface.class),
    TEENSY_MASTER(ConfigData.TEENSY_MASTER_IP, ConfigData.TEENSY_MASTER_PORT, /*SocketServerInterface.class*/SerialInterface.class),
    TEENSY_MASTER_MONTHLERY(ConfigData.TEENSY_MASTER_IP, ConfigData.TEENSY_MASTER_PORT, SerialInterface.class),
    TEENSY_SLAVE(ConfigData.TEENSY_SLAVE_IP, ConfigData.TEENSY_SLAVE_PORT, SerialInterface.class),
    ELECTRON(ConfigData.ELECTRON_IP, ConfigData.ELECTRON_PORT, SocketClientInterface.class, false),

    LOCALHOST_SERVER(ConfigData.LOCALHOST, ConfigData.LOCALSERVER_PORT, SocketServerInterface.class),
    LOCALHOST_CLIENT(ConfigData.LOCALHOST, ConfigData.LOCALSERVER_PORT, SocketClientInterface.class),

    MASTER_LL_SIMULATEUR(ConfigData.LOCALHOST, ConfigData.LL_MASTER_SIMULATEUR, SocketClientInterface.class),
    SLAVE_SIMULATEUR(ConfigData.LOCALHOST, ConfigData.HL_SLAVE_SIMULATEUR, SocketClientInterface.class),

    DEBUG_SIMULATEUR(ConfigData.LOCALHOST, ConfigData.DEBUG_SIMULATEUR_PORT, SocketClientInterface.class),
    DEBUG_SIMULATEUR_SERVER(ConfigData.LOCALHOST, ConfigData.DEBUG_SIMULATEUR_PORT, SocketServerInterface.class),

    BALISE_IMAGE(ConfigData.BALISE_IP,ConfigData.BALISE_PORT,SocketClientInterface.class),
    BALISE_IA(ConfigData.BALISE_IP,ConfigData.IA_PORT,SocketClientInterface.class),
    ;

    /**
     * clef config pour trouver l'ip si instanciation
     */
    private ConfigInfo<String> ipKey;

    /**
     * clef config pour trouver le port si instanciation
     */
    private ConfigInfo<Integer> portKey;

    /**
     * Communication Interface à instancier
     */
    private Class<?> aClass;
    private boolean mandatory;

    /**
     * Interface de communication à ajouter
     */
    private CommunicationInterface communicationInterface;

    /**
     * Construit une connection obligatoire
     * @param ipKey     clef config pour récupérer l'ip
     * @param portKey   clef config pour le port
     * @param c         type de Communication Interface à instancier
     */
    Connection(ConfigInfo<String> ipKey, ConfigInfo<Integer> portKey, Class<?> c) {
        this(ipKey, portKey, c, true);
    }

    /**
     * Construit une connection
     * @param ipKey     clef config pour récupérer l'ip
     * @param portKey   clef config pour le port
     * @param c         type de Communication Interface à instancier
     * @param mandatory cette connexion est-elle obligatoire pour démarrer?
     */
    Connection(ConfigInfo<String> ipKey, ConfigInfo<Integer> portKey, Class<?> c, boolean mandatory) {
        this.ipKey = ipKey;
        this.portKey = portKey;
        this.aClass = c;
        this.mandatory = mandatory;
    }

    /**
     * Initialise la connexion
     * @param config    config à utiliser pour aller chercher les infos ip
     * @throws CommunicationException
     *                  in case of communication problem
     */
    public void init(Config config) throws CommunicationException {
        Constructor constructor = aClass.getDeclaredConstructors()[0];
        try {
            this.communicationInterface = (CommunicationInterface) constructor
                    .newInstance(config.getString(this.ipKey), config.getInt(this.portKey), isMandatory());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CommunicationException("Impossible d'instancier l'interface de comm");
        }
        this.communicationInterface.init();
    }

    /**
     * Ré-initialise la connexion en cas de perte en cours de match
     * @throws CommunicationException
     *                  in case of communication problem
     */
    public void reInit() throws CommunicationException {
        Log.COMMUNICATION.critical("Had to reset connection "+this+"!");
        this.communicationInterface.close();
        this.communicationInterface.init();
    }

    /**
     * Envoie un message
     * @param message   le message à envoyer
     * @throws CommunicationException
     *                  in case of communication problem
     */
    public boolean send(String message) throws CommunicationException {
        if(!isMandatory() && !communicationInterface.isInterfaceOpen())
            return false;
        while(!this.communicationInterface.isInterfaceOpen()) {
            try {
                Log.COMMUNICATION.critical(this.name()+" WAITING FOR OPENNESS");
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.communicationInterface.send(message);
    }

    /**
     * Renvoie le message reçu
     * @return  le message s'il y en a un
     * @throws CommunicationException
     *                  in case of communication problem
     */
    public Optional<String> read() throws CommunicationException {
        if(!isMandatory() && !communicationInterface.isInterfaceOpen())
            return Optional.empty();
        while(!this.communicationInterface.isInterfaceOpen()) {
            try {
                Log.COMMUNICATION.critical(this.name()+" WAITING FOR OPENNESS", 1);
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.communicationInterface.read();
    }

    /**
     * Ferme la connexion
     * @throws CommunicationException
     *                  in case of communication problem
     */
    public void close() throws CommunicationException {
        this.communicationInterface.close();
    }

    /**
     * @return true si la connection est prête
     */
    public boolean isInitiated() {
        if(communicationInterface == null)
            return false;
        return communicationInterface.isInterfaceOpen();
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
