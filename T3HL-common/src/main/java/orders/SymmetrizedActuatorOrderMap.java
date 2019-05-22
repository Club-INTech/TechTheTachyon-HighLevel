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
    private Map<ActuatorsOrder, ActuatorsOrder> correspondenceMap = new HashMap<ActuatorsOrder, ActuatorsOrder>();

    /**
     * construit la map de correspondances
     */
    private SymmetrizedActuatorOrderMap(){
        correspondenceMap.put(ActuatorsOrder.FERME_PORTE_DROITE, ActuatorsOrder.FERME_PORTE_GAUCHE);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ACCELERATEUR, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ACCELERATEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ACCELERATEUR, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ACCELERATEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_8_PALETS, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_8_PALETS);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_8_PALETS, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_8_PALETS);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI);

        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_GOLDONIUM, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_GOLDONIUM);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_GOLDONIUM, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_GOLDONIUM);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_BALANCE, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_BALANCE);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_BALANCE, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_BALANCE);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_TIENT_BLEU, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_TIENT_BLEU);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_TIENT_BLEU, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_TIENT_BLEU);
        correspondenceMap.put(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET, ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
        correspondenceMap.put(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
        correspondenceMap.put(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET, ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_GAUCHE_DE_UN_PALET);
        correspondenceMap.put(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_GAUCHE_DE_UN_PALET, ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET);
        correspondenceMap.put(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
        correspondenceMap.put(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
        correspondenceMap.put(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET, ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
        correspondenceMap.put(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET, ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
        correspondenceMap.put(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_DROIT_DE_UN_PALET, ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
        correspondenceMap.put(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
        correspondenceMap.put(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE, ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);
        correspondenceMap.put(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE, ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
        correspondenceMap.put(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE);
        correspondenceMap.put(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
        correspondenceMap.put(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE, ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
        correspondenceMap.put(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
        correspondenceMap.put(ActuatorsOrder.TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_DROIT, ActuatorsOrder.TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_GAUCHE);
        correspondenceMap.put(ActuatorsOrder.TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_GAUCHE, ActuatorsOrder.TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_DROIT);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_INTERMEDIAIRE, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_INTERMEDIAIRE);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_INTERMEDIAIRE, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_INTERMEDIAIRE);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE);
        correspondenceMap.put(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE);
        correspondenceMap.put(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT);
        correspondenceMap.put(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR, ActuatorsOrder.REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
        correspondenceMap.put(ActuatorsOrder.REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR, ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_PALET);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_PALET, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_LIBERE_ASCENSEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_LIBERE_ASCENSEUR, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART_INTERMEDIAIRE, ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART_INTERMEDIAIRE);
        correspondenceMap.put(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART_INTERMEDIAIRE, ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART_INTERMEDIAIRE);
    }

    /**
     * @param order l'actionneur à symétriser
     * @return l'actionneur à symétriser
     */
    public Order getSymmetrizedActuatorOrder(ActuatorsOrder order)
    {
        return correspondenceMap.getOrDefault(order,order);
    }

    @Override
    public void updateConfig(Config config) {}
}
