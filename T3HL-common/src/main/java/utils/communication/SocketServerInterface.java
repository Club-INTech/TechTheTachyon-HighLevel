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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Définit l'interface de connexion utilisant une socket serveur
 * @see SocketInterface
 *
 * @author william, rem
 */
public class SocketServerInterface extends SocketInterface {

    private ServerSocket serverSocket = null;

    /**
     * Construit une interface de connexion point-à-point attendant la connexion
     * @param ipAddress     ip à laquelle se connecté
     * @param port          port de connexion du serveur
     */
    public SocketServerInterface(String ipAddress, int port) {
        super(ipAddress, port);
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
                    serverSocket = new ServerSocket(port);
                    serverSocket.setSoTimeout(CONNECTION_TIMEOUT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (!isInterrupted()) {
                    try {
                        synchronized (this) {
                            try {
                                Log.COMMUNICATION.debug(String.format("Waiting connection on port %d", port));
                                privSocket = serverSocket.accept();
                                if (privSocket != null){
                                    socket = privSocket;
                                }
                                Log.COMMUNICATION.debug(String.format("Connection accepted on port %d", port));
                                initBuffers();
                                Log.COMMUNICATION.debug(String.format("Connection initialized on port %d", port));
                            } catch (SocketTimeoutException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException | CommunicationException e) {
                        e.printStackTrace();
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
