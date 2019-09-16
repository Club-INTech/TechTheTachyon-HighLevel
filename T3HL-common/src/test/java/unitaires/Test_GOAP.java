package unitaires;

import ai.goap.Action;
import ai.goap.ActionGraph;
import ai.goap.Agent;
import ai.goap.EnvironmentInfo;
import data.XYO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.HLInstance;
import utils.Log;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Test_GOAP {

    /**
     * Graphe et actions de test basés sur cet article: https://gamedevelopment.tutsplus.com/tutorials/goal-oriented-action-planning-for-a-smarter-ai--cms-20793
     */
    private ActionGraph firewoodGraph;
    private ActionGraph firewoodGraph2; // prend en compte un déplacement de l'agent
    private Action collectBranches;
    private Action getAxe;
    private Action chopLog;
    private Action getAxe2;
    private Action chopLog2;

    private Vec2 getAxePosition = new InternalVectCartesian(-100f, -100f);
    private Vec2 chopLogPosition = new InternalVectCartesian(100f, 100f);


    @Before
    public void init() {
        HLInstance hl = HLInstance.getInstance("Master");
        Assert.assertNotNull(hl.getConfig());
        Log.init();
        getAxe = new Action() {
            {
                preconditions.put("axe in inventory", false);
                effects.put("axe in inventory", true);
            }

            @Override
            public double getCost(EnvironmentInfo info) {
                return 2;
            }

            @Override
            public void perform(EnvironmentInfo info) {}

            @Override
            public boolean isComplete(EnvironmentInfo info) {
                return true; // immédiat
            }

            @Override
            public boolean requiresMovement(EnvironmentInfo info) {
                return false;
            }

            @Override
            public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {}

            @Override
            public void reset() {}

            @Override
            public String toString() {
                return "getAxe";
            }
        };

        getAxe2 = new Action() {
            {
                preconditions.put("axe in inventory", false);
                effects.put("axe in inventory", true);
            }

            @Override
            public double getCost(EnvironmentInfo info) {
                return 2;
            }

            @Override
            public void perform(EnvironmentInfo info) {}

            @Override
            public boolean isComplete(EnvironmentInfo info) {
                return true; // immédiat
            }

            @Override
            public boolean requiresMovement(EnvironmentInfo info) {
                return true;
            }

            @Override
            public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {
                targetPos.set(getAxePosition);
            }

            @Override
            public void reset() {}

            @Override
            public String toString() {
                return "getAxe2";
            }
        };

        chopLog = new Action() {
            {
                preconditions.put("axe in inventory", true);
                effects.put("make firewood", true);
            }

            @Override
            public double getCost(EnvironmentInfo info) {
                return 4;
            }

            @Override
            public void perform(EnvironmentInfo info) {}

            @Override
            public boolean isComplete(EnvironmentInfo info) {
                return true; // immédiat
            }

            @Override
            public boolean requiresMovement(EnvironmentInfo info) {
                return false;
            }

            @Override
            public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {}

            @Override
            public void reset() {}

            @Override
            public String toString() {
                return "chopLog";
            }
        };

        chopLog2 = new Action() {
            {
                preconditions.put("axe in inventory", true);
                effects.put("make firewood", true);
            }

            @Override
            public double getCost(EnvironmentInfo info) {
                return 4;
            }

            @Override
            public void perform(EnvironmentInfo info) {}

            @Override
            public boolean isComplete(EnvironmentInfo info) {
                return true; // immédiat
            }

            @Override
            public boolean requiresMovement(EnvironmentInfo info) {
                return true;
            }

            @Override
            public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {
                targetPos.set(chopLogPosition);
            }

            @Override
            public void reset() {}

            @Override
            public String toString() {
                return "chopLog2";
            }
        };

        collectBranches = new Action() {
            {
                effects.put("make firewood", true);
            }

            @Override
            public double getCost(EnvironmentInfo info) {
                return 8;
            }

            @Override
            public void perform(EnvironmentInfo info) {}

            @Override
            public boolean isComplete(EnvironmentInfo info) {
                return true; // immédiat
            }

            @Override
            public boolean requiresMovement(EnvironmentInfo info) {
                return false;
            }

            @Override
            public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {}

            @Override
            public void reset() {}

            @Override
            public String toString() {
                return "collectBranches";
            }
        };

        firewoodGraph = new ActionGraph();
        firewoodGraph.node(chopLog);
        firewoodGraph.node(collectBranches);
        firewoodGraph.node(getAxe);

        firewoodGraph2 = new ActionGraph();
        firewoodGraph2.node(chopLog2);
        firewoodGraph2.node(collectBranches);
        firewoodGraph2.node(getAxe2);
    }

    @Test
    public void planning() {
        Map<String, Object> baseState = new HashMap<>();
        baseState.put("axe in inventory", false);
        baseState.put("make firewood", false);
        EnvironmentInfo base = new EnvironmentInfo(null, baseState, null);

        Map<String, Object> goalState = new HashMap<>();
        goalState.put("make firewood", true);
        EnvironmentInfo goal = new EnvironmentInfo(null, goalState, null);
        Stack<ActionGraph.Node> plan = firewoodGraph.plan(base, goal);
        Assert.assertEquals(getAxe, plan.pop().getAction());
        Assert.assertEquals(chopLog, plan.pop().getAction());
        Assert.assertTrue(plan.isEmpty());
    }

    @Test
    public void finiteStateMachine() {
        XYO xyo = new XYO(new InternalVectCartesian(0f, 0f), 0.0);
        Map<String, Object> baseState = new HashMap<>();
        baseState.put("axe in inventory", false);
        baseState.put("make firewood", false);

        Map<String, Object> goalState = new HashMap<>();
        goalState.put("make firewood", true);
        EnvironmentInfo goal = new EnvironmentInfo(xyo, goalState, null);

        EnvironmentInfo info = new EnvironmentInfo(xyo, baseState, null);
        Agent agent = new Agent(firewoodGraph) {
            @Override
            public EnvironmentInfo gatherEnvironmentInformation() {
                return info;
            }

            @Override
            protected void orderMove(Vec2 position) {}
        };
        agent.setCurrentGoal(goal);

        Assert.assertEquals(agent.getIdleState(), agent.getFiniteStateMachine().peekState());
        agent.step();
        // on est dans l'état Performing (getAxe)
        Assert.assertEquals(agent.getPerformingState(), agent.getFiniteStateMachine().peekState());
        agent.step();
        // on est dans l'état Performing (chopLog)
        Assert.assertEquals(agent.getPerformingState(), agent.getFiniteStateMachine().peekState());
        agent.step();
        // on est dans l'état Idle (on a plus rien à faire)
        Assert.assertEquals(agent.getIdleState(), agent.getFiniteStateMachine().peekState());
        agent.step();
    }

    @Test
    public void movingState() {
        XYO xyo = new XYO(new InternalVectCartesian(0f, 0f), 0.0);
        Map<String, Object> baseState = new HashMap<>();
        baseState.put("axe in inventory", false);
        baseState.put("make firewood", false);

        Map<String, Object> goalState = new HashMap<>();
        goalState.put("make firewood", true);
        EnvironmentInfo goal = new EnvironmentInfo(xyo, goalState, null);

        EnvironmentInfo fakeInfo = new EnvironmentInfo(xyo, baseState, null);
        Agent agent = new Agent(firewoodGraph2) {
            @Override
            public EnvironmentInfo gatherEnvironmentInformation() {
                return fakeInfo;
            }

            @Override
            protected void orderMove(Vec2 position) {}
        };
        agent.setCurrentGoal(goal);

        Assert.assertEquals(agent.getIdleState(), agent.getFiniteStateMachine().peekState()); // recherche du plan
        agent.step();
        // on est dans l'état Performing (getAxe)
        Assert.assertEquals(agent.getPerformingState(), agent.getFiniteStateMachine().peekState());
        agent.step();

        // on est dans l'état Moving (il faut aller à la position de la hache)
        Assert.assertEquals(agent.getMovingState(), agent.getFiniteStateMachine().peekState());
        agent.step();
        fakeInfo.getXYO().getPosition().set(getAxePosition); // téléportation sur la hache!

        // on se rend compte qu'on est à la bonne position
        Assert.assertEquals(agent.getMovingState(), agent.getFiniteStateMachine().peekState());
        agent.step();

        // on est dans l'état Performing (getAxe - confirmation de l'action)
        Assert.assertEquals(agent.getPerformingState(), agent.getFiniteStateMachine().peekState());
        agent.step();

        // on est dans l'état Performing (chopLog)
        Assert.assertEquals(agent.getPerformingState(), agent.getFiniteStateMachine().peekState());
        agent.step();

        // on est dans l'état Moving (il faut aller à la position du bois)
        Assert.assertEquals(agent.getMovingState(), agent.getFiniteStateMachine().peekState());
        agent.step();
        fakeInfo.getXYO().getPosition().set(chopLogPosition); // téléportation sur l'arbre!

        // on se rend compte qu'on est à la bonne position
        Assert.assertEquals(agent.getMovingState(), agent.getFiniteStateMachine().peekState());
        agent.step();

        // on est dans l'état Performing (chopLog - confirmation de l'action)
        Assert.assertEquals(agent.getPerformingState(), agent.getFiniteStateMachine().peekState());
        agent.step();

        // on est dans l'état Idle (on a plus rien à faire)
        Assert.assertEquals(agent.getIdleState(), agent.getFiniteStateMachine().peekState());
        agent.step();
    }
}