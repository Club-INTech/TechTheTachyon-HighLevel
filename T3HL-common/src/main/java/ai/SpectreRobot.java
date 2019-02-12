package ai;

import data.Graphe;
import data.Table;
import locomotion.Pathfinder;
import utils.math.Vec2;

import java.util.List;
import java.util.stream.Collectors;

public class SpectreRobot {

    private final Pathfinder spectrePathfinder;
    private final Graphe baseGraphe;
    private final Table baseTable;
    private final Graphe fakeGraphe;
    private final Table fakeTable;

    public SpectreRobot(Graphe baseGraphe, Table baseTable) {
        this.baseGraphe = baseGraphe;
        this.baseTable = baseTable;

        this.fakeTable = new Table();

        this.fakeGraphe = new Graphe(fakeTable);

        copyFromBase(fakeGraphe, fakeTable);
        this.spectrePathfinder = new Pathfinder(baseGraphe);
    }

    public SpectreRobot deepCopy() {
        SpectreRobot copy = new SpectreRobot(baseGraphe, baseTable);
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
        List<Vec2> mobileObstacles = baseTable.getMobileObstacles().stream()
                .map(t -> t.getShape().getCenter())
                .collect(Collectors.toList());

        fakeGraphe.getNodes().clear();
        fakeGraphe.getNodes().addAll(baseGraphe.getNodes());
        fakeGraphe.getRidges().clear();
        fakeGraphe.getRidges().addAll(baseGraphe.getRidges());

        fakeTable.getFixedObstacles().clear();
        fakeTable.getFixedObstacles().addAll(baseTable.getFixedObstacles());
        fakeTable.updateMobileObstacles(mobileObstacles);
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
