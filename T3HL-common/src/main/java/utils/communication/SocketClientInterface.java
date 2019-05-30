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
import java.net.Socket;

/**
 * Interface Socket client
 * @see SocketInterface
 *
 * @author rem, william
 */
public class SocketClientInterface extends SocketInterface {

    /**
     * Construit une interface de connexion point-à-point se connectant directement à un port ouvert
     * @param ipAdress  adresse ip
     * @param port      port auquel se connecter
     */
    public SocketClientInterface(String ipAdress, int port, boolean mandatory) {
        super(ipAdress, port, mandatory);
    }

    @Override
    public synchronized void init() throws CommunicationException {
        this.initiated = false;
        Thread thread = new Thread(() -> {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < SocketInterface.CONNECTION_TIMEOUT || !isMandatory()) {
                synchronized (this) {
                    try {
                        Log.COMMUNICATION.debug(String.format("Trying to connect to %s on port %d",ipAddress, port));
                        this.socket = new Socket(ipAddress, port);
                        Log.COMMUNICATION.debug(String.format("Socket created to %s on port %d", ipAddress, port));
                        this.initBuffers(socket);
                        break;
                    } catch (IOException | CommunicationException e) {
                        Log.COMMUNICATION.warning(String.format("Failed trying to connect to %s on port %d",ipAddress, port));
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (System.currentTimeMillis() - start > SocketClientInterface.CONNECTION_TIMEOUT && isMandatory()) {
                Log.COMMUNICATION.critical(String.format("FAILED to connect to %s on port %d", ipAddress, port));
            }
            else{
                Log.COMMUNICATION.debug(String.format("SUCCESS to connect to %s on port %d", ipAddress, port));
            }
        });
        thread.setName("SocketClient("+ipAddress+":"+port+")");
        thread.start();
    }
}
