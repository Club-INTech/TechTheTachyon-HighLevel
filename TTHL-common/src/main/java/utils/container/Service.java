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

import pfg.config.Config;

/**
 * Interface servant à définir un service : un service est un singleton qui doit implémenter la méthode updateConfig,
 * et être instancié par le container
 *
 * @author pf
 */
public interface Service
{
    /**
     * Cette méthode est appelée par le container après instanciation du service.
     * Elle sert à attribuer à des attributs des valeurs contenus dans la config.
     */
    void updateConfig(Config config);
}
