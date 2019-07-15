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

package scripts;

import utils.HLInstance;
import utils.container.Module;

import java.util.HashMap;

/**
 * Module de gestion des scripts
 *
 * @author rem
 */
public abstract class ScriptManager implements Module {

    /**
     * Container pour construire les scripts
     */
    protected HLInstance hl;

    /**
     * Dictionnaire faisant le liens entre nom de script et script
     */
    protected HashMap<ScriptNames, Script> instanciedScripts;

    /**
     * Construit le script manager
     * @param hl
     *              container pour instancier les scripts
     */
    protected ScriptManager(HLInstance hl) {
        this.hl = hl;
        this.instanciedScripts = new HashMap<>();
    }

    /**
     * Methode permettant d'avoir un script instancié
     * @param name
     *              nom du script
     * @return      le script recherché
     */
    public Script getScript(ScriptNames name) {
        Script script = instanciedScripts.get(name);
        if (script == null) {
            throw new IllegalArgumentException("Le script " + name + " n'a pas été instancié");
        }
        return script;
    }
}
