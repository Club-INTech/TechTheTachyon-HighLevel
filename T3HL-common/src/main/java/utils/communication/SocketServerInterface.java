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

package utils.communication;

import utils.Log;

import java.io.IOException;
import java.net.*;
import java.util.Optional;

/**
 * Définit l'interface de connexion utilisant une socket serveur
 * @see SocketInterface
 *
 * @author william, rem
 */
public class SocketServerInterface extends SocketInterface {

    private ServerSocket serverSocket = null;
    private int receivedCount;

    /**
     * Construit une interface de connexion point-à-point attendant la connexion
     * @param ipAddress     ip à laquelle se connecté
     * @param port          port de connexion du serveur
     */
    public SocketServerInterface(String ipAddress, int port, boolean mandatory) {
        super(ipAddress, port, mandatory);
    }

    @Override
    public void init() {
        this.initiated = false;
        Thread thread = new Thread(){
            @Override
            public void run() {
                Socket privSocket = null;
                try {
                    Log.COMMUNICATION.debug(String.format("Creating socket waiting connection on port %d", port));
                    serverSocket = new ServerSocket();
                    int receiveBufferSize = serverSocket.getReceiveBufferSize();
                    Log.COMMUNICATION.debug("Receive buffer size on "+serverSocket.getLocalSocketAddress()+" is "+receiveBufferSize);
                    serverSocket.setReceiveBufferSize(receiveBufferSize*200);
                    serverSocket.setSoTimeout(CONNECTION_TIMEOUT);
                    serverSocket.bind(new InetSocketAddress(port));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (!isInterrupted()) {
                    try {
                        synchronized (this) {
                            try {
                                Log.COMMUNICATION.debug(String.format("Waiting connection on port %d", port));
                                privSocket = serverSocket.accept();
                                if (privSocket != null) {
                                    socket = privSocket;
                                    socket.setTcpNoDelay(true);
                                }
                                Log.COMMUNICATION.debug(String.format("Connection accepted on port %d", port));
                                initBuffers(privSocket);
                                Log.COMMUNICATION.debug(String.format("Connection initialized on port %d", port));
                                send("ping");
                                Log.COMMUNICATION.debug("Initialized connection with a 'ping'");
                            } catch (SocketTimeoutException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException | CommunicationException e) {
                        e.printStackTrace();
                        Log.COMMUNICATION.critical("Socket error: "+e.getMessage());
                        if(e instanceof SocketException) {
                            if(((SocketException) e).getMessage().contains("Socket is closed")) {
                                break;
                            }
                        }
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public synchronized Optional<String> read() throws CommunicationException {
        Optional<String> result = super.read();
        if(result.isPresent()) {
            receivedCount++;
            Log.COMMUNICATION.debug(">> READ: "+result.get());
        }
        return result;
    }

    @Override
    public synchronized void close() throws CommunicationException {
        super.close();
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
