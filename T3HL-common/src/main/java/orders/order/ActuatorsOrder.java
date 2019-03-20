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
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_INTERMEDIAIRE("posinter right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_INTERMEDIAIRE("posinter left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR("dist right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR("dist left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL("grnd right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL("grnd left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR("stock right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR("stock left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR("acc right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR("acc left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_GOLDONIUM("gold right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_GOLDONIUM("gold left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_BALANCE("bal right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_BALANCE("bal left"),
    MONTE_ASCENCEUR_DROIT_DE_UN_PALET("up right", false, false),
    MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET("up left", false, false),
    DESCEND_ASCENSEUR_DROIT_DE_UN_PALET("down right", false, false),
    DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET("down left", false, false),
    ACTIVE_LA_POMPE_DROITE("suck right", true),
    ACTIVE_LA_POMPE_GAUCHE("suck left", true),
    DESACTIVE_LA_POMPE_DROITE("unsuck right", true),
    DESACTIVE_LA_POMPE_GAUCHE("unsuck left", true),
    ACTIVE_ELECTROVANNE_DROITE("valveon right", true),
    ACTIVE_ELECTROVANNE_GAUCHE("valveon left", true),
    DESACTIVE_ELECTROVANNE_DROITE("valveoff right", true),
    DESACTIVE_ELECTROVANNE_GAUCHE("valveoff left", true), // TODO
    ENVOIE_UN_XL_A_ANGLE_VOULU("XLm"),
    ENVOIE_UN_XL_A_LA_VITESSE_VOULUE("XLs"),
    TEST_PALET_ATTRAPÉ_EN_FONCTION_DU_COUPLE_DROIT("torqueBras right"),
    TEST_PALET_ATTRAPÉ_EN_FONCTION_DU_COUPLE_GAUCHE("torqueBras left"),
    TEST_COUPLE_XL("torqueXL"),
    ;

    /**
     * Ordre envoyé au LL
     */
    private String orderStr;

    /**
     * Doit-on attendre que l'actionneur ait fini son action?
     */
    private boolean waitForFinish;

    /**
     * Cet ordre incrémente-t-il l'indice de synchronisation? (mis à false pour les ordres qui sont fait en parallèle d'autres dans le LL, eg les ascenseurs)
     */
    private final boolean incrementWaitingIndex;

    /**
     * Constructeur qui ne précise pas la durée l'action
     * @param orderStr action à faire
     */
    ActuatorsOrder(String orderStr){
        this(orderStr, false, true);
    }

    /**
     * Constructeur qui précise l'action et sa durée
     * @param orderStr : action à faire
     * @param waitForFinish doit-on attendre la fin de l'action?
     */
    ActuatorsOrder(String orderStr, boolean waitForFinish) {
        this(orderStr, waitForFinish, true);
    }


    /**
     * Constructeur qui précise l'action et sa durée
     * @param orderStr : action à faire
     * @param waitForFinish doit-on attendre la fin de l'action?
     * @param incrementWaitingIndex cet ordre incrémente-t-il l'indice de synchronisation? (mis à false pour les ordres qui sont fait en parallèle d'autres dans le LL, eg les ascenseurs)
     *                              'waitForFinish' est ignoré si ce paramètre est à 'true'
     */
    ActuatorsOrder(String orderStr, boolean waitForFinish, boolean incrementWaitingIndex) {
        this.orderStr = orderStr;
        this.waitForFinish = waitForFinish;
        this.incrementWaitingIndex = incrementWaitingIndex;
    }

    /**
     * Getter de l'ordrer envoyé au LL
     * @return ordre en string
     */
    public String getOrderStr(){
        return this.orderStr;
    }

    /**
     * Doit-on attendre que l'actionneur est fini son action?
     * @return 'true' si on doit attendre
     */
    public boolean shouldWaitForFinish() {
        return waitForFinish;
    }

    /**
     * Cet ordre incrémente-t-il l'indice de synchronisation? (mis à false pour les ordres qui sont fait en parallèle d'autres dans le LL, eg les ascenseurs)
     * @return
     */
    public boolean shouldIncrementWaitingIndex() {
        return incrementWaitingIndex;
    }
}
