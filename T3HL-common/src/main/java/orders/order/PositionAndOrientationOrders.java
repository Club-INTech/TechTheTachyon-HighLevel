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

package orders.order;

import lowlevel.order.Order;
import lowlevel.order.OrderBuilder;
import lowlevel.order.OrderWithArgument;
import utils.communication.Formatting;

/**
 * Enum qui contient tous les ordres concernant la position et l'orientation du robot
 *
 * @author yousra, jglrxavpok
 */
public final class PositionAndOrientationOrders {
    public static Order AskPosition = OrderBuilder.createSimple("?xyo");
    public static OrderWithArgument SetPositionAndOrientation = OrderBuilder.createWithArgs("cxyo", Formatting.INTEGER, Formatting.INTEGER, Formatting.FLOAT5);
    public static OrderWithArgument SetOrientation = OrderBuilder.createWithArgs("co", Formatting.FLOAT5);
    public static Order AskSickData = OrderBuilder.createSimple("lectureSICK");
}
