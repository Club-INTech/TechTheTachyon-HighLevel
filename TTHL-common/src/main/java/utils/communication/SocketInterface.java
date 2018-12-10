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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Interface Socket client
 * @see CommunicationInterface
 *
 * @author rem, william
 */
public abstract class SocketInterface implements CommunicationInterface {
    /**
     * Adresse ip à laquelle se connecter
     */
    protected String ipAddress;

    /**
     * Port auquel se connecter
     */
    protected int port;

    /**
     * Socket
     */
    protected Socket socket;

    /**
     * Buffer de lecture
     */
    private BufferedReader input;

    /**
     * Buffer d'écriture
     */
    private BufferedWriter output;

    /**
     * True si la connexion a été initialisée
     */
    private boolean initiate;

    /**
     * Timeout de connexion du server
     */
    public static final int CONNECTION_TIMEOUT  = 120000;

    /**
     * Construit une interface de connexion socket
     * @param ipAddress     l'addresse ip sur laquelle se connecter
     * @param port          port de connexion
     */
    public SocketInterface(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.initiate = false;
    }

    @Override
    public synchronized void send(String message) throws CommunicationException {
        try {
            this.output.write(message);
            this.output.newLine();
            this.output.flush();
        } catch (IOException e) {
            throw new CommunicationException("Envoie du message " + message + " impossible");
        }
    }

    @Override
    public synchronized Optional<String> read() throws CommunicationException {
        Optional<String> message = Optional.empty();
        try {
            if (this.input.ready() && initiate) {
                message = Optional.of(input.readLine());
            }
        } catch (IOException e) {
            throw new CommunicationException("Lecture du message impossible : la connexion est fermée");
        }
        return message;
    }

    @Override
    public synchronized void close() throws CommunicationException {
        try {
            if (this.initiate) {
                this.input.close();
                this.output.close();
                this.socket.close();
                this.initiate = false;
            }
        } catch (IOException e) {
            throw new CommunicationException("Impossible de fermer la communication");
        }
    }

    @Override
    public boolean isInterfaceOpen() {
        return initiate;
    }

    /**
     * Initialise les buffers IO
     * @throws CommunicationException
     *                  en cas de problème d'initialisation des buffers
     */
    protected void initBuffers() throws CommunicationException {
        try {
            this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8.name()));
            this.output = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8.name()));
            this.initiate = true;
        } catch (IOException e) {
            throw new CommunicationException("Impossible de créer les buffers IO");
        }
    }

    /**
     * Getter & Setter
     */
    public boolean isInitiate() {
        return initiate;
    }
}
