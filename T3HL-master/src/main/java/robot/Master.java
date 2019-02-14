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

import data.CouleurPalet;
import locomotion.Locomotion;
import orders.OrderWrapper;
import orders.hooks.HookFactory;
import pfg.config.Config;

import java.util.Stack;

/**
 * robot.Robot principale : on rassemble ici tout ce qui est unique au robot principale
 *
 * @author rem
 */
public class Master extends Robot {
    private Stack<CouleurPalet> ascenseurGauche;
    private Stack<CouleurPalet> ascenseurDroite;

    public Master(Locomotion locomotion, OrderWrapper orderWrapper, HookFactory hookFactory) {
        super(locomotion, orderWrapper, hookFactory);
        this.ascenseurGauche = new Stack<>();
        this.ascenseurDroite = new Stack<>();
    }

    public int getNbpaletsdroits (){
        return ascenseurDroite.size();
    }

    public int getNbpaletsgauches(){return ascenseurGauche.size();}

    public void pushPaletDroit(CouleurPalet colour) {
        ascenseurDroite.push(colour);
    }

    public void pushPaletGauche(CouleurPalet colour) {
        ascenseurGauche.push(colour);
    }

    public void popPaletDroit(){
        ascenseurDroite.pop();
    }

    public void popPaletGauche(){
        ascenseurGauche.pop();
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
