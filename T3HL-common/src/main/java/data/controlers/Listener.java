/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.

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

package data.controlers;

import connection.Connection;
import connection.ConnectionManager;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.communication.CommunicationException;
import utils.container.ServiceThread;

import java.util.*;

/**
 * Ecoute toutes les connections instanciées
 *
 * @author rem
 */
public class Listener extends ServiceThread {

    /**
     * Temps d'attente entre chaque boucle pour l'initialisation des connexions
     */
    public static final int TIME_LOOP =   200;

    /**
     * Gestionnaire des connexions
     */
    private ConnectionManager connectionManager;

    /**
     * Pour connaître les redirections à effectuer
     */
    private boolean master;

    /**
     * Map des cannaux & queues des services
     */
    private Map<Channel, Collection<String>> collectionMap;

    /**
     * Si on est en simulation
     */
    private boolean simulation;

    /**
     * Est-ce qu'on utilise le Lidar?
     */
    private boolean useLidar;

    private boolean visualize;
    /**
     * Est-ce qu'on active l'éléctron ?
     */
    private boolean useElectron;

    /**
     * Construit un listener
     * @param connectionManager     gestionnaire des connexions
     */
    public Listener(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.collectionMap = new HashMap<>();
    }

    /**
     * Redirige les messages vers les bons services pour qu'ils soient traités
     * @param header    sert à identifier le cannal
     * @param message   corps du message
     */
    private void handleMessage(String header, String message) {
      //  System.out.println("RECEIVED ON HEADER '"+header+"': "+message);
        for (Channel registeredChannel : collectionMap.keySet()) {
            if (registeredChannel.getHeaders().equals(header)) {
                collectionMap.get(registeredChannel).add(message);
            }
        }
    }

    /**
     * Ajoute un liens channel -> queue
     * @param channel   le channel à mapper
     * @param queue     la queue sur laquelle le mapper
     */
    public void addCollection(Channel channel, Collection<String> queue){
        collectionMap.put(channel, queue);
    }

    @Override
    public void run() {
        Connection buddy;
        if (simulation){
            buddy = Connection.SLAVE_SIMULATEUR;
        }
        else {
            buddy = Connection.SLAVE;
        }
        // Initialisation des connexions
        Log.COMMUNICATION.debug("Listener lancé : connection aux devices...");
        try {
            if (simulation){
                Log.COMMUNICATION.debug("Simulation initializing connections...");
                connectionManager.initConnections(Connection.MASTER_LL_SIMULATEUR);
                Log.COMMUNICATION.debug("Simulated Teensy Master connected");
                // FIXME/TODO connectionManager.initConnections(Connection.SLAVE_SIMULATEUR);
                // FIXME/TODO Log.COMMUNICATION.debug("Simulated Buddy connected");
                Log.COMMUNICATION.debug("Debug connection init...");
                connectionManager.initConnections(Connection.DEBUG_SIMULATEUR);
                Log.COMMUNICATION.debug("Debug connection ready!");
            }
            else {
                // FIXME :) connectionManager.initConnections(Connection.BALISE);
                Log.COMMUNICATION.debug("Lidar");
                Log.COMMUNICATION.debug("Balise");
                if (master) {
                    connectionManager.initConnections(/*Connection.SLAVE, */Connection.TEENSY_MASTER);
                    Log.COMMUNICATION.debug("Slave");
                    Log.COMMUNICATION.debug("Teensy Master");
                } else {
                    connectionManager.initConnections(Connection.MASTER, Connection.TEENSY_SLAVE);
                    buddy = Connection.MASTER;
                    Log.COMMUNICATION.debug("Master");
                    Log.COMMUNICATION.debug("Teensy Slave");
                }

                if(visualize) {
                    Log.COMMUNICATION.debug("Debug connection init...");
                    connectionManager.initConnections(Connection.DEBUG_SIMULATEUR);
                    Log.COMMUNICATION.debug("Debug connection ready!");
                }
            }
            if(useLidar) {
                connectionManager.initConnections(Connection.LIDAR_DATA);
                Log.COMMUNICATION.debug("Lidar connected");
            }
            if(useElectron) {
                connectionManager.initConnections(Connection.ELECTRON);
                Log.COMMUNICATION.debug("Electron connected");
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
        while (!connectionManager.areConnectionsInitiated()) {
            try {
                Thread.sleep(TIME_LOOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.COMMUNICATION.debug("Listener opérationnel");

        // Main loop
        String header;
        String message;
        Optional<String> buffer;
        Iterator<Connection> iterator;
        Connection current;
        while (!Thread.currentThread().isInterrupted()) {
            iterator = connectionManager.getInitiatedConnections().iterator();
            while (iterator.hasNext()){
                current = iterator.next();
                try {
                    buffer = current.read();
                    if (buffer.isPresent()) {
                        if(buffer.get().length() >= 2) {
                            header = buffer.get().substring(0, 2);
                            message = buffer.get().substring(2);
                            handleMessage(header, message);
                            if (header.equals(Channel.ROBOT_POSITION.getHeaders())) {
                                if(buddy.isInitiated()) {
                                    Log.COMMUNICATION.debug("Sending buddy pos to "+buddy+" because of message '"+message+"' from "+current);
                                    buddy.send(String.format("%s%s", Channel.BUDDY_POSITION.getHeaders(), message));
                                }
                            }
                        }
                    }
                } catch (CommunicationException e) {
                    e.printStackTrace();
                    Log.COMMUNICATION.critical("Impossible de lire sur la connexion " + current.name() + " : fermeture de la connexion");
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            connectionManager.closeInitiatedConnections();
            Log.COMMUNICATION.warning("Listener interrompu : fermeture de toute les connexions");
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateConfig(Config config) {
        this.master = config.getBoolean(ConfigData.MASTER);
        this.simulation=config.getBoolean(ConfigData.SIMULATION);
        this.useLidar = config.getBoolean(ConfigData.USING_LIDAR);
        this.useElectron = config.getBoolean(ConfigData.USING_ELECTRON);
        this.visualize = config.getBoolean(ConfigData.VISUALISATION);
    }
}
