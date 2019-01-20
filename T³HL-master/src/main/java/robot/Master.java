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

package robot;

import locomotion.Locomotion;
import orders.OrderWrapper;
import orders.hooks.HookFactory;
import pfg.config.Config;
import robot.Robot;

/**
 * robot.Robot principale : on rassemble ici tout ce qui est unique au robot principale
 *
 * @author rem
 */
public class Master extends Robot {
    private int nbpaletsdroits;
    private int nbpaletsgauches;
    public Master(Locomotion locomotion, OrderWrapper orderWrapper, HookFactory hookFactory, int nbpaletsdroits) {
        super(locomotion, orderWrapper, hookFactory);
        this.nbpaletsdroits = nbpaletsdroits;

    }

    public int getNbpaletsdroits (){
        return this.nbpaletsdroits;
    }
    public void decrement(){
        this.nbpaletsdroits=this.nbpaletsdroits-1;
    }

    public int getNbpaletsgauches(){return this.nbpaletsgauches;}
    public void decrementgauche(){this.nbpaletsgauches=this.nbpaletsgauches-1;}
    /** TODO implémenter un ordre sur le couple des XL pour remplacer les nbpaletsdroits et nbpaletsgauches*/

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
