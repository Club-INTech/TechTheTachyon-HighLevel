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
     * Est-ce que cette connexion est obligatoire pour démarrer?
     */
    private final boolean mandatory;

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
    protected BufferedReader input;

    /**
     * Buffer d'écriture
     */
    protected BufferedWriter output;

    /**
     * True si la connexion a été initialisée
     */
    protected boolean initiated;

    /**
     * Timeout de connexion du server
     */
    public static final int CONNECTION_TIMEOUT  = 120000;

    /**
     * Construit une interface de connexion socket
     * @param ipAddress     l'addresse ip sur laquelle se connecter
     * @param port          port de connexion
     */
    public SocketInterface(String ipAddress, int port, boolean mandatory) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.initiated = false;
        this.mandatory = mandatory;
    }

    @Override
    public abstract void init() throws CommunicationException;

    @Override
    public synchronized boolean send(String message) throws CommunicationException {
        try {
            if (this.initiated) {
                this.output.write(message);
                this.output.newLine();
                this.output.flush();
                return true;
            }
        } catch (IOException e) {
            throw new CommunicationException("Envoie du message " + message + " impossible");
        }
        return false;
    }

    @Override
    public synchronized Optional<String> read() throws CommunicationException {
        Optional<String> message = Optional.empty();
        try {
            if (this.initiated && this.input.ready()) {
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
            if (this.initiated) {
                this.initiated = false;
                this.input.close();
                this.input = null;
                this.output.close();
                this.output = null;
                this.socket.close();
                this.socket = null;
                System.gc();
            }
        } catch (IOException e) {
            throw new CommunicationException("Impossible de fermer la communication");
        }
    }

    @Override
    public boolean isInterfaceOpen() {
        return initiated;
    }

    /**
     * Initialise les buffers IO
     * @throws CommunicationException
     *                  en cas de problème d'initialisation des buffers
     */
    protected void initBuffers(Socket socket) throws CommunicationException {
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8.name()), 64*1024);
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8.name()), 64*1024);
            this.initiated = true;
        } catch (IOException e) {
            throw new CommunicationException("Impossible de créer les buffers IO");
        }
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Getter & Setter
     */
    public boolean isInitiated() {
        return initiated;
    }
}
