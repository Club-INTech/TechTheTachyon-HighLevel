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

package orders.hooks;

import lowlevel.order.Order;
import pfg.config.Config;
import orders.OrderWrapper;
import utils.Log;
import utils.container.Module;

import java.util.ArrayList;

/**
 * Classe permettant de gérer les hooks via une enum : pour créer un hook, il suffit de l'ajouter dans l'enum HookNames
 * Les hooks sont configurés (=envoyés au LL) via la méthode configureHook, à appeler en début de match ou de script
 *
 * @author yousra
 */
public class HookFactory implements Module {

    /** OrderWrapper */
    private OrderWrapper orderWrapper;

    /** Liste des Hooks */
    private ArrayList<HookNames> configuredHook = new ArrayList<HookNames>();

     /**
     *Constructeur en privé car déjà instancié par le container
     */
    private HookFactory (OrderWrapper orderWrapper){
        this.orderWrapper=orderWrapper;
    }

    /**
     * Configure les hooks en paramètre (envoie toutes les infos au LL)
     * @param hooks hooks à configurer
     */
    public void configureHooks(HookNames... hooks) {
        Order sentOrder;
        for(HookNames hook:hooks){

            sentOrder=hook.getOrder();

            if (configuredHook.contains(hook)){
                Log.HOOK.warning("Hook déjà configuré : on ne fait rien");
                break;
            }
            orderWrapper.configureHook(hook.getId(), hook.getPosition(), hook.getTolerency(), hook.getOrientation(),hook.getTolerencyAngle(), sentOrder);
            Log.HOOK.debug("Hook " + hook.getDeclaringClass() + " : Configuré");
            configuredHook.add(hook);
        }
    }

    /**
     * Active les hooks en paramètres
     * Balance un WARNING si le hook n'a pas été configuré (et ne fait rien du coup...)
     * @param hooks hooks à activer
     */
    public void enableHook(HookNames... hooks){
        for(HookNames hook:hooks){
            if (!configuredHook.contains(hook)){
                Log.HOOK.warning("Hook " + hook.getDeclaringClass().getName() + " : Non configuré ! Ne peut etre activé");
                break;
            }
            orderWrapper.enableHook(hook);
            Log.HOOK.debug("Hook " + hook.getDeclaringClass().getName() + " : Activé");
        }
    }

    /**
     * Desactive les hooks en paramètres
     * Balance un WARNING si le hook n'a pas été configuré
     * @param hooks hooks que l'on veut desactiver
     */
    public void disableHook(HookNames... hooks){
        for(HookNames hook:hooks){
            if(!configuredHook.contains(hook)){
                Log.HOOK.warning("Hook " + hook.getDeclaringClass().getName() + " : Non configuré ! Ne peut etre désactivé");
                break;
            }
            orderWrapper.disableHook(hook);
            Log.HOOK.debug("Hook " + hook.getDeclaringClass().getName() + " : Désactivé");
        }
    }

    /**
     * Active tous les hooks configurés
     */
    public void enableConfiguredHooks(){
        for(HookNames hook:configuredHook){
            orderWrapper.enableHook(hook);
            Log.HOOK.debug("Hook " + hook.getDeclaringClass().getName() + " : Activé");
        }
    }

    /**
     * Désactive tous les hooks configurés
     */
    public void disableConfiguredHooks(){
        for(HookNames hook:configuredHook){
            orderWrapper.disableHook(hook);
            Log.HOOK.debug("Hook " + hook.getDeclaringClass().getName() + " : Désactivé");
        }
    }

}


