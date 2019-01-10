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

package orders.order;

/**
 * Enum qui contient tous les ordres donnés aux actionneurs (baisser ou relever un bras par exemple)
 *
 * @author yousra
 */
public enum ActuatorsOrder implements Order {
    //Exemples
    OUVRE_PORTE_AVANT("olpAv"),
    FERME_PORTE_AVANT("flpAv"),
    OUVRE_PORTE_ARRIERE("olpAr"),
    FERME_PORTE_ARRIERE("flpAr"),
    FERME_PORTE_DROITE("flpd"),
    FERME_PORTE_GAUCHE("flpg"),
    ENVOIE_LE_BRAS_A_LA_POSITION_DISTRIBUTEUR_DROIT("dist right"),
    ENVOIE_LE_BRAS_A_LA_POSITION_DISTRIBUTEUR_GAUCHE("dist left"),
    ENVOIE_LE_BRAS_A_LA_POSITION_SOL("grnd"),
    ENVOIE_LE_BRAS_A_LA_POSITION_ASCENSEUR("stock"),
    ENVOIE_LE_BRAS_A_LA_POSITION_ACCELERATEUR("acc"),
    ENVOIE_LE_BRAS_A_LA_POSITION_GOLDONIUM("gold"),
    ENVOIE_LE_BRAS_A_LA_POSITION_BALANCE("bal"),
    MONTE_ASCENCEUR_DE_UN_PALET("up"),
    DESCEND_ASCENSEUR_DE_UN_PALET("down"),
    ACTIVE_LA_POMPE("suck"),
    DESACTIVE_LA_POMPE("unsuck"),
    ACTIVE_ELECTROVANNE("valveon"),
    DESACTIVE_ELECTROVANNE("valveoff"),


    ;

    /**
     * Ordre envoyé au LL
     */
    private String orderStr;
    
    /**
     * Durée de l'action en ms
     */
    private int actionDuration;

    /**
     * Constructeur qui ne précise pas la durée l'action
     * @param orderStr action à faire
     */
    ActuatorsOrder(String orderStr){
        this(orderStr, 0);
    }

    /**
     * Constructeur qui précise l'action et sa durée
     * @param orderStr : action à faire 
     * @param actionDuration : durée de l'action
     */
    ActuatorsOrder(String orderStr, int actionDuration){
        this.orderStr=orderStr;
        this.actionDuration=actionDuration;
    }

    /**
     * Getter de l'ordrer envoyé au LL
     * @return ordre en string
     */
    public String getOrderStr(){
        return this.orderStr;
    }

    /**
     * Getter de la durée de l'action
     * @return durée de l'action
     */
    public int getActionDuration(){
        return this.actionDuration;
    }

   
}
