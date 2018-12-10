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

package orders;

import pfg.config.Config;
import orders.order.ActuatorsOrder;
import orders.order.Order;
import utils.container.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe qui permet de symétriser tous les ordres
 *
 * @author yousra
 */
public class SymmetrizedActuatorOrderMap implements Service {

    /**
     * Map contenant un actionneur pour clé, et son symétrique pour valeur
     */
    Map<ActuatorsOrder, ActuatorsOrder> correspondenceMap = new HashMap<ActuatorsOrder, ActuatorsOrder>();

    /**
     * construit la map de correspondances
     */
    private SymmetrizedActuatorOrderMap(){
        correspondenceMap.put(ActuatorsOrder.FERME_PORTE_DROITE, ActuatorsOrder.FERME_PORTE_GAUCHE);
    }

    /**
     * @param order l'actionneur à symétriser
     * @return l'actionneur à symétriser
     */
    public Order getSymmetrizedActuatorOrder(ActuatorsOrder order)
    {
        return correspondenceMap.get(order);
    }

    @Override
    public void updateConfig(Config config) {

    }
}
