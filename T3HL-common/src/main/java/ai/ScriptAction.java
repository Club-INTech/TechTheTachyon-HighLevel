package ai;

import ai.goap.Action;
import ai.goap.EnvironmentInfo;
import data.Graphe;
import data.Table;
import data.graphe.Node;
import locomotion.NoPathFound;
import scripts.Script;
import scripts.ScriptNames;
import utils.Log;
import utils.math.Vec2;

public class ScriptAction extends Action {

    /**
     * Le script à exécuter
     */
    private final Script script;

    /**
     * Le nom du script
     */
    private final ScriptNames scriptName;

    /**
     * Coût de base du script (sans compter le déplacement vers son entryPosition)
     */
    private final double baseCost;

    /**
     * La version du script à exécuter
     */
    private final int version;

    /**
     * Le script a-t-il été exécuté?
     */
    private boolean executed;

    public ScriptAction(ScriptNames scriptName, int version) {
        this(scriptName, version, 0.0);
    }

    public ScriptAction(ScriptNames scriptName, int version, double baseCost) {
        this.baseCost = baseCost;
        this.version = version;
        this.scriptName = scriptName;
        this.script = scriptName.getScript();
    }

    @Override
    public boolean arePreconditionsMet(EnvironmentInfo info) {
        return super.arePreconditionsMet(info) && checkPath(info);
    }

    private boolean checkPath(EnvironmentInfo info) {
        boolean result = false;
        Graphe graph = info.getSpectre().getSimulatedGraph();
        Vec2 entryPos = script.entryPosition(version).getCenter();
        graph.writeLock().lock();
        Vec2 currentPos = info.getCurrentPosition();
        Node start = graph.addProvisoryNode(currentPos);
        Node aim = graph.addProvisoryNode(entryPos);
        Table table = info.getSpectre().getSimulatedTable();

        if (table.isPositionInFixedObstacle(entryPos) || table.isPositionInFixedObstacle(currentPos)) {
            Log.LOCOMOTION.warning("Points de départ " + currentPos + " ou d'arrivée " + entryPos + " dans un obstacle");
        }
        graph.writeLock().unlock();
        try {
            graph.readLock().lock();
            info.getSpectre().getSimulationPathfinder().findPath(start, aim);
            result = true;
        } catch (NoPathFound f) {
            System.out.println(">> "+toString());
            f.printStackTrace(); // TODO: debug only
        } finally {
            graph.readLock().unlock();
            graph.writeLock().lock();
            graph.removeProvisoryNode(start);
            graph.removeProvisoryNode(aim);
            graph.writeLock().unlock();
        }
        return result;
    }

    @Override
    public double getCost(EnvironmentInfo info) {
        Vec2 entryPos = script.entryPosition(version).getCenter();
        return baseCost + info.getXYO().getPosition().distanceTo(entryPos) + Math.abs(info.getXYO().getPosition().angleTo(entryPos));
    }

    @Override
    public void perform(EnvironmentInfo info) {
        script.goToThenExecute(version);
        executed = true;
    }

    @Override
    public boolean isComplete(EnvironmentInfo info) {
        return executed;
    }

    @Override
    public boolean requiresMovement(EnvironmentInfo info) {
        return true;
    }

    @Override
    public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {
        Vec2 entryPos = script.entryPosition(version).getCenter();
        targetPos.set(entryPos);
    }

    @Override
    public void reset() {

    }

    @Override
    public String toString() {
        return "ScriptAction("+scriptName+")";
    }
}
