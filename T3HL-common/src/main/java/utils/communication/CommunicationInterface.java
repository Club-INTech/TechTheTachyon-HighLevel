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

import java.util.Optional;

/**
 * Définit les fonctionnalitées d'une interface de communication
 *
 * @author rem, william
 */
public interface CommunicationInterface {
    /**
     * Méthode pour envoyer un message
     * @param message   le corps du message
     * @return un booléen pour savoir si l'envoi a réussi
     * @throws CommunicationException
     *                  en cas de problèmes de communication
     */
    boolean send(String message) throws CommunicationException;

    /**
     * Méthode de lecture d'un message
     * @return  le message ou null si l'on a rien recu
     * @throws CommunicationException
     *                  en cas de problèmes de communication
     */
    Optional<String> read() throws CommunicationException;

    /**
     * Initialise la connection
     * @throws CommunicationException
     *                  en cas de problèmes de communication
     */
    void init() throws CommunicationException;

    /**
     * Ferme la connection
     * @throws CommunicationException
     *                  en cas de problèmes de communication
     */
    void close() throws CommunicationException;

    /**
     * @return true si l'on peut communiquer avec cette interface
     */
    boolean isInterfaceOpen();

    /**
     * Est-ce qu'on peut démarrer sans cette connexion?
     * @return 'true' si on peut démarrer sans cette connexion, 'false' si non
     */
    boolean isMandatory();
}
