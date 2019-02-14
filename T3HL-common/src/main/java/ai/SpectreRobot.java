/**
 * Copyright (c) 2019, INTech.
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

package ai;

import data.Graphe;
import data.Table;
import locomotion.Pathfinder;
import pfg.config.Config;
import utils.Log;
import utils.math.Vec2;

import java.util.List;
import java.util.stream.Collectors;

public class SpectreRobot {

    private final Pathfinder spectrePathfinder;
    private final Graphe baseGraphe;
    private final Table baseTable;
    private final Graphe fakeGraphe;
    private final Table fakeTable;
    private final Config config;

    public SpectreRobot(Graphe baseGraphe, Table baseTable, Config config) {
        this.baseGraphe = baseGraphe;
        this.baseTable = baseTable;
        this.config = config;

        this.fakeTable = new Table();
        // copie des obstacles, oui c'est dégueulasse
        fakeTable.paletRougeGauche = baseTable.paletRougeGauche;
        fakeTable.paletRougeDroite = baseTable.paletRougeDroite;
        fakeTable.paletVertDroite = baseTable.paletVertDroite;
        fakeTable.paletBleuDroite = baseTable.paletBleuDroite;
        fakeTable.paletVertGauche = baseTable.paletVertGauche;
        fakeTable.paletBleuGauche = baseTable.paletBleuGauche;
        fakeTable.zoneChaosDroite = baseTable.zoneChaosDroite;
        fakeTable.zoneChaosGauche = baseTable.zoneChaosGauche;
        this.fakeGraphe = new Graphe(fakeTable);

        updateConfig(config);
        copyFromBase(fakeGraphe, fakeTable);

        this.spectrePathfinder = new Pathfinder(fakeGraphe);
    }

    public SpectreRobot deepCopy() {
        SpectreRobot copy = new SpectreRobot(fakeGraphe, fakeTable, config); // la copie doit partir de l'état de ce spectre
        //SpectreRobot copy = new SpectreRobot(baseGraphe, baseTable);
        // TODO
        return copy;
    }

    /**
     * Le spectre se remet dans les conditions réelles
     * Utilisé avant la planification pour remettre à jour le graphe et la table
     */
    public void comeBackToReality() {
        copyFromBase(baseGraphe, baseTable);
    }

    public void copyFromBase(Graphe baseGraphe, Table baseTable) {
        Log.AI.debug("Copie à partir de la base");
        List<Vec2> mobileObstacles = baseTable.getMobileObstacles().stream()
                .map(t -> t.getShape().getCenter())
                .collect(Collectors.toList());
        fakeTable.getFixedObstacles().clear();
        fakeTable.getFixedObstacles().addAll(baseTable.getFixedObstacles());
        fakeTable.updateTableAfterFixedObstaclesChanges();
        fakeTable.updateMobileObstacles(mobileObstacles);
    }

    public void updateConfig(Config config) {
        fakeTable.updateConfig(config);
        fakeGraphe.updateConfig(config);
    }

    public Graphe getSimulatedGraph() {
        return fakeGraphe;
    }

    public Table getSimulatedTable() {
        return fakeTable;
    }

    public Pathfinder getSimulationPathfinder() {
        return spectrePathfinder;
    }
}
