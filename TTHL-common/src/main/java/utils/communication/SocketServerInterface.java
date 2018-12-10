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

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Définit l'interface de connexion utilisant une socket serveur
 * @see SocketInterface
 *
 * @author william, rem
 */
public class SocketServerInterface extends SocketInterface {

    /**
     * Construit une interface de connexion point-à-point attendant la connexion
     * @param ipAddress     ip à laquelle se connecté
     * @param port          port de connexion du serveur
     */
    public SocketServerInterface(String ipAddress, int port) {
        super(ipAddress, port);
    }

    @Override
    public void init() throws CommunicationException {
        new Thread(() -> {
            try {
                synchronized (this) {
                    ServerSocket serverSocket = new ServerSocket(port);
                    serverSocket.setSoTimeout(CONNECTION_TIMEOUT);
                    this.socket = serverSocket.accept();
                    this.initBuffers();
                }
            } catch (IOException | CommunicationException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
