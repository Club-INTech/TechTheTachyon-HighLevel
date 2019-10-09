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
import data.MessageHandler;
import data.synchronization.SynchronizationWithBuddy;
import pfg.config.Configurable;
import utils.HLInstance;
import utils.Log;
import utils.communication.CommunicationException;
import utils.container.Module;
import utils.container.ModuleThread;

import java.util.*;

/**
 * Ecoute toutes les connections instanciées et dispatche les messages reçus aux gestionnaires appropriés
 *
 * @author rem, jglrxavpok
 */
public class Listener implements Module {

    /**
     * Temps d'attente entre chaque boucle pour l'initialisation des connexions
     */
    public static final int TIME_LOOP =   200;

    private HLInstance hl;
    /**
     * Gestionnaire des connexions
     */
    private ConnectionManager connectionManager;
    private SynchronizationWithBuddy buddySync;

    /**
     * Pour connaître les redirections à effectuer
     */
    @Configurable
    private boolean master;

    /**
     * Map des cannaux & queues des services
     */
    private Map<Channel, Collection<String>> collectionMap;

    /**
     * Si on est en simulation
     */
    @Configurable
    private boolean simulation;

    /**
     * Est-ce qu'on utilise le Lidar?
     */
    @Configurable
    private boolean usingLidar;

    /**
     * Si on utilise la balise pour le traitement d'images
     */
    @Configurable
    private boolean usingBaliseImage;

    @Configurable
    private boolean zoneChaosTest;

    @Configurable
    private boolean visualisation;
    /**
     * Est-ce qu'on active l'électron ?
     */
    @Configurable
    private boolean usingElectron;

    /**
     * Est-ce qu'on se connecte au copain?
     */
    @Configurable
    private boolean connectToBuddy;

    /**
     * Temps entre deux envois de positions au buddy
     */
    @Configurable
    private long timeBetweenPosUpdates;

    /**
     * Date du dernier envoi de position
     */
    private long lastTimeSinceBuddyUpdate;
    private List<Channel> channelList;

    /**
     * Header -> Liste des handlers correspondants
     */
    private Map<String, List<MessageHandler>> messageHandlers = new HashMap<>();
    private boolean activated;

    /**
     * Construit un listener
     * @param connectionManager     gestionnaire des connexions
     */
    public Listener(HLInstance hl, ConnectionManager connectionManager, SynchronizationWithBuddy buddySync) {
        this.hl = hl;
        this.connectionManager = connectionManager;
        this.buddySync = buddySync;
        this.collectionMap = new HashMap<>();
        this.channelList = new LinkedList<>();
      //  this.setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Redirige les messages vers les bons services pour qu'ils soient traités
     * @param header    sert à identifier le cannal
     * @param message   corps du message
     */
    private void handleMessage(String header, String message) {
        // TODO: Remplacer complétement par le système des MessageHandler

        boolean handled = false;
        for (int i = 0; i < channelList.size(); i++) {
            Channel registeredChannel = channelList.get(i);
            if (registeredChannel.getHeaders().equals(header)) {
                collectionMap.get(registeredChannel).add(message);
                handled = true;
            }
        }

        if(handled) // on évite de lancer des événements pour un canal déjà géré
            return;

        List<MessageHandler> handlers = messageHandlers.get(header);
        if(handlers == null) return;
        for (int i = 0; i < handlers.size(); i++) {
            final MessageHandler handler = handlers.get(i);
            hl.async("Handle Message", () -> handler.handle(message));
        }
    }

    /**
     * Ajoute un liens channel -> queue
     * @param channel   le channel à mapper
     * @param queue     la queue sur laquelle le mapper
     */
    public void addCollection(Channel channel, Collection<String> queue){
        collectionMap.put(channel, queue);
        channelList.add(channel);
    }

    @Override
    public void onInit(HLInstance hl) {
        hl.async("Async Listener init", ()->asyncInit(hl));
    }

    private void asyncInit(HLInstance hl) {
        Connection buddy = Connection.SLAVE;
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
                Log.COMMUNICATION.debug("Lidar");
                Log.COMMUNICATION.debug("Balise");
                if (master) {
                    connectionManager.initConnections(Connection.TEENSY_MASTER);
                    Log.COMMUNICATION.debug("Teensy Master");
                    buddy = Connection.SLAVE;
                    if(connectToBuddy) {
                        connectionManager.initConnections(buddy);
                        Log.COMMUNICATION.debug("Slave");
                    }
                } else {
                    connectionManager.initConnections(Connection.TEENSY_SLAVE);
                    Log.COMMUNICATION.debug("Teensy Slave");
                    buddy = Connection.MASTER;
                    if(connectToBuddy) {
                        connectionManager.initConnections(buddy);
                        Log.COMMUNICATION.debug("Master");
                    }
                }

                buddySync.setConnection(buddy);

                if(visualisation) {
                    Log.COMMUNICATION.debug("Debug connection init...");
                    connectionManager.initConnections(Connection.DEBUG_SIMULATEUR);
                    Log.COMMUNICATION.debug("Debug connection ready!");
                }
            }
            if(usingBaliseImage || zoneChaosTest){
                //if(usingBaliseImage ){
                connectionManager.initConnections(Connection.BALISE_IMAGE);
            }
            if(usingLidar) {
                connectionManager.initConnections(Connection.LIDAR_DATA);
                Log.COMMUNICATION.debug("Lidar connected");
            }
            if(usingElectron) {
                connectionManager.initConnections(Connection.ELECTRON);
                Log.COMMUNICATION.debug("Electron connected");
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
        while (!connectionManager.areMandatoryConnectionsInitiated()) {
            try {
                Thread.sleep(TIME_LOOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.COMMUNICATION.debug("Listener opérationnel");

        ArrayList<Connection> initiatedConnections = connectionManager.getInitiatedConnections();
        LinkedList<Connection> toClose = new LinkedList<>();
        final Connection buddyConnection = buddy;
        for (int i = 0; i < initiatedConnections.size(); i++) {
            Connection current = initiatedConnections.get(i);
            hl.async("Read connection "+current, () -> {
                while(true) {
                    try {
                        Optional<String> buffer = current.read();
                        if(buffer.isPresent()) {
                            if (buffer.get().length() >= 2) {
                                String header = buffer.get().substring(0, 2);
                                String message = buffer.get().substring(2);
                                handleMessage(header, message);
                                if (header.equals(Channel.ROBOT_POSITION.getHeaders())) {
                                    if (buddyConnection.isInitiated() && System.currentTimeMillis() - lastTimeSinceBuddyUpdate >= timeBetweenPosUpdates) {
                                        buddySync.sendPosition();
                                        lastTimeSinceBuddyUpdate = System.currentTimeMillis();
                                    }
                                }
                            }
                        }
                    } catch (CommunicationException e) {
                        e.printStackTrace();
                        Log.COMMUNICATION.critical("Impossible de lire sur la connexion " + current.name() + " : fermeture de la connexion");
                        break;
                    }
                }
            });
        }
        activated = true;
    }

    public boolean hasFinishedLoading() {
        return activated;
    }

    /* TODO
    @Override
    public void interrupt() {
        try {
            connectionManager.closeInitiatedConnections();
            Log.COMMUNICATION.warning("Listener interrompu : fermeture de toute les connexions");
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }*/

    public void registerMessageHandler(Channel channel, MessageHandler handler) {
        List<MessageHandler> messageHandlerList = messageHandlers.getOrDefault(channel.getHeaders(), new LinkedList<>());
        messageHandlerList.add(handler);
        messageHandlers.put(channel.getHeaders(), messageHandlerList);
    }
}
