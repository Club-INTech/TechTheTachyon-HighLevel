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
import utils.ConfigData;
import utils.communication.CommunicationException;
import utils.communication.CommunicationInterface;
import utils.communication.SocketClientInterface;
import utils.communication.SocketServerInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Defines all the communication interfaces
 *
 * @author william, rem
 */
public enum Connection {
    MASTER(ConfigData.MASTER_IP, ConfigData.MASTER_PORT, SocketClientInterface.class),
    SLAVE(ConfigData.LOCALHOST, ConfigData.MASTER_PORT, SocketServerInterface.class),
    LIDAR(ConfigData.LOCALHOST, ConfigData.LIDAR_PORT, SocketClientInterface.class),
    TEENSY_MASTER(ConfigData.TEENSY_MASTER_IP, ConfigData.TEENSY_MASTER_PORT, SocketClientInterface.class),
    TEENSY_SLAVE(ConfigData.TEENSY_SLAVE_IP, ConfigData.TEENSY_SLAVE_PORT, SocketClientInterface.class),

    LOCALHOST_SERVER(ConfigData.LOCALHOST, ConfigData.LOCALSERVER_PORT, SocketServerInterface.class),
    LOCALHOST_CLIENT(ConfigData.LOCALHOST, ConfigData.LOCALSERVER_PORT, SocketClientInterface.class)
    ;

    /**
     * clef config pour trouver l'ip si instanciation
     */
    private ConfigData ipKey;

    /**
     * clef config pour trouver le port si instanciation
     */
    private ConfigData portKey;

    /**
     * Communication Interface à instancier
     */
    private Class<?> aClass;

    /**
     * Interface de communication à ajouter
     */
    private CommunicationInterface communicationInterface;

    /**
     * Construit une connection
     * @param ipKey     clef config pour récupérer l'ip
     * @param portKey   clef config pour le port
     * @param c         type de Communication Interface à instancier
     */
    Connection(ConfigData ipKey, ConfigData portKey, Class<?> c) {
        this.ipKey = ipKey;
        this.portKey = portKey;
        this.aClass = c;
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
                    .newInstance(config.getString(this.ipKey), config.getInt(this.portKey));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CommunicationException("Impossible d'instancier l'interface de comm");
        }
        this.communicationInterface.init();
    }

    /**
     * Envoie un message
     * @param message   le message à envoyer
     * @throws CommunicationException
     *                  in case of communication problem
     */
    public void send(String message) throws CommunicationException {
        this.communicationInterface.send(message);
    }

    /**
     * Renvoie le message reçu
     * @return  le message s'il y en a un
     * @throws CommunicationException
     *                  in case of communication problem
     */
    public Optional<String> read() throws CommunicationException {
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
        return communicationInterface.isInterfaceOpen();
    }
}
