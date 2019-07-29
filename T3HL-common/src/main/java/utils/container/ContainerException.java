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

package utils.container;

/**
 * Exception levé par le container en cas de dépendance circulaire ou de dépendances mal gérée entre les classes
 *
 * @since 2019 'extends' RuntimeException pour ne pas être obligé de la catch. Vaut mieux faire vraiment planter le HL
 * si une telle erreur arrive: c'est qu'il se passe quelque chose de grave, ou que quelqu'un sait pas coder.
 *
 * @author pf, jglrxavpok
 */
public class ContainerException extends RuntimeException
{
    public ContainerException() {super();}
    public ContainerException(String message) {super(message);}
    public ContainerException(Exception reason) {super(reason);}
}
