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
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR("musclor right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_INTERMEDIAIRE("posinter right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_INTERMEDIAIRE("posinter left"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_INTERMEDIAIRE("posinter right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR("dist right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI("dist right noretry"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI("dist left noretry"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR("dist left"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR("distSecondaire right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL("grnd right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL("grnd left"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_SOL("grnd right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR("stock right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR("stock left"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR("stockSecondaire right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT("stockDepot right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT("stockDepot left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR("acc right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR("acc left"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR("acc right"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_SECONDAIRE("accSecondaire right"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR2_SECONDAIRE("accSecondaire2 right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_GOLDONIUM("gold right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_GOLDONIUM("gold left"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM("gold right"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM_DEPOT("goldDepot right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_BALANCE("balP right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_BALANCE("balP left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_TIENT_BLEU("holdBlue right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_TIENT_BLEU("holdBlue left"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_BALANCE("bal right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE("brasRecule right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE("brasRecule left"),
    ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_RECULE("brasRecule right"),
    POUSSE_LE_PALET_BRAS_DROIT("pushPalet right"),
    POUSSE_LE_PALET_BRAS_GAUCHE("pushPalet left"),
    POUSSE_LE_PALET_BRAS_DU_SECONDAIRE("pushPalet right"),
    REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR("dist2stock left"),
    REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR("dist2stock right"),
    REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR("dist2stock right"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_PALET("overPuck right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET("overPuck left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ACCELERATEUR("accMiddle right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ACCELERATEUR("accMiddle left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT("accMiddleDepot right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT("accMiddleDepot left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS("acc7Depot right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS("acc7Depot left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_8_PALETS("acc8Depot right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_8_PALETS("acc8Depot left"),
    ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR("freeElevator right"),
    ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_LIBERE_ASCENSEUR("freeElevator left"),
    MONTE_ASCENCEUR_DROIT_DE_UN_PALET("up right"),
    MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET("up left"),
    MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET("updown right"),
    MONTE_DESCEND_ASCENCEUR_GAUCHE_DE_UN_PALET("updown left"),
    MONTE_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET("up right"),
    MONTE_DESCEND_ASCENCEUR_DU_SECONDAIRE_DE_UN_PALET("updown right"),
    DESCEND_ASCENSEUR_DROIT_DE_UN_PALET("down right"),
    DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET("down left"),
    DESCEND_MONTE_ASCENCEUR_DROIT_DE_UN_PALET("downup right"),
    DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET("downup left"),
    DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET("down right"),
    DESCEND_MONTE_ASCENCEUR_DU_SECONDAIRE_DE_UN_PALET("downup right"),
    ACTIVE_LA_POMPE_DROITE("suck right"),
    ACTIVE_LA_POMPE_GAUCHE("suck left"),
    ACTIVE_LA_POMPE_DU_SECONDAIRE("suck right"),
    DESACTIVE_LA_POMPE_DROITE("unsuck right"),
    DESACTIVE_LA_POMPE_GAUCHE("unsuck left"),
    DESACTIVE_LA_POMPE_DU_SECONDAIRE("unsuck right"),
    ACTIVE_ELECTROVANNE_DROITE("valveon right", 300),
    ACTIVE_ELECTROVANNE_GAUCHE("valveon left", 300),
    ACTIVE_ELECTROVANNE_DU_SECONDAIRE("valveon right", 300),
    DESACTIVE_ELECTROVANNE_DROITE("valveoff right", 300),
    DESACTIVE_ELECTROVANNE_GAUCHE("valveoff left", 300),
    DESACTIVE_ELECTROVANNE_DU_SECONDAIRE("valveoff right", 300),


    ENVOIE_UN_XL_A_ANGLE_VOULU("XLm"),
    ENVOIE_UN_XL_A_LA_VITESSE_VOULUE("XLs"),
    TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_DROIT("torqueBras right"),
    TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_GAUCHE("torqueBras left"),
    TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_DU_SECONDAIRE("torqueBras right"),

    CRACHE_UN_PALET("oust"),
    RANGE_CRACHE_PALET("range"),
    TEST_COUPLE_XL("torqueXL"),

    ;

    /**
     * Utilisé pour tester tous les positions au hasard et pour avoir une synchronisation correcte avec le LL pour les mouvements de bras
     */
    public static final ActuatorsOrder[] ARM_ORDERS = {
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_INTERMEDIAIRE,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_INTERMEDIAIRE,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_INTERMEDIAIRE,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_SOL,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_SECONDAIRE,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_GOLDONIUM,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_GOLDONIUM,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM_DEPOT,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_BALANCE,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_BALANCE,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_BALANCE,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE,
            ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_RECULE,
            POUSSE_LE_PALET_BRAS_DROIT,
            POUSSE_LE_PALET_BRAS_GAUCHE,
            POUSSE_LE_PALET_BRAS_DU_SECONDAIRE,
            REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR,
            REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR,
            REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_PALET,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ACCELERATEUR,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ACCELERATEUR,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_8_PALETS,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_8_PALETS,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_TIENT_BLEU,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_TIENT_BLEU,
            ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR,
            ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_LIBERE_ASCENSEUR,
    };

    static {
        for(ActuatorsOrder order : ARM_ORDERS) {
            order.setArmOrder();
        }
    }

    /**
     * Ordre envoyé au LL
     */
    private String orderStr;
    private long actionDuration;

    /**
     * Cet ordre est-il un ordre qui bouge un bras?
     */
    private boolean isArmOrder = false;

    /**
     * Constructeur qui ne précise pas la durée l'action
     */
    ActuatorsOrder(String orderStr) {
        this(orderStr, 0);
    }

    ActuatorsOrder(String orderStr, long actionDuration) {
        this.orderStr = orderStr;
        this.actionDuration = actionDuration;
    }

    /**
     * Permet d'indiquer que l'ordre concerne un bras (utile pour le simulateur)
     */
    private void setArmOrder() {
        this.isArmOrder = true;
    }

    public boolean isArmOrder() {
        return isArmOrder;
    }

    /**
     * Durée de l'action
     * @return long, durée de l'action
     */
    public long getActionDuration() {
        return actionDuration;
    }

    /**
     * Getter de l'ordrer envoyé au LL
     * @return ordre en string
     */
    public String getOrderStr(){
        return this.orderStr;
    }

}
