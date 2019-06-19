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

package connection;

import pfg.config.Config;
import utils.communication.CommunicationException;
import utils.container.Module;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Gère l'instanciation et l'iniitalisation des interfaces de communication
 *
 * @author william, rem
 */
public class ConnectionManager implements Module {

    /**
     * Config pour récupérer ip & port
     */
    private Config config;

    /**
     * Connections initiés
     */
    private ArrayList<Connection> initiatedConnections;

    /**
     * Pour le container
     */
    public ConnectionManager() {
        this.initiatedConnections = new ArrayList<>();
    }

    /**
     * Initialise les connections voulus
     * @param connections   les connexions à initialiser
     * @throws CommunicationException
     *                      en cas de problemes d'initialisation
     */
    public void initConnections(Connection... connections) throws CommunicationException {
        for (Connection connection : connections) {
            connection.init(this.config);
            initiatedConnections.add(connection);
        }
    }

    /**
     * Ferme toute les connections
     * @throws CommunicationException
     *                      en cas de problemes de connexion
     */
    public void closeInitiatedConnections() throws CommunicationException {
        Iterator<Connection> iterator = initiatedConnections.iterator();
        Connection current;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.close();
            iterator.remove();
        }
    }

    /**
     * @return true si les connexions instanciées sont prêtes
     */
    public boolean areMandatoryConnectionsInitiated() {
        for (Connection connection : initiatedConnections) {
            if (connection.isMandatory() && !connection.isInitiated()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see Object#finalize()
     */
    @Override
    protected void finalize() {
        try {
            this.closeInitiatedConnections();
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see Module#updateConfig(Config)
     */
    @Override
    public void updateConfig(Config config) {
        this.config = config;
    }

    /**
     * Getter
     */
    public ArrayList<Connection> getInitiatedConnections() {
        return initiatedConnections;
    }
}
