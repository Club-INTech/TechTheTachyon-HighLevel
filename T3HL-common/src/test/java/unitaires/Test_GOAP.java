package unitaires;

import ai.goap.*;
import org.junit.Assert;
import org.junit.Test;
import utils.math.Vec2;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Test_GOAP {

    @Test
    public void planning() {
        // https://gamedevelopment.tutsplus.com/tutorials/goal-oriented-action-planning-for-a-smarter-ai--cms-20793
        Action getAxe = new Action() {
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

        Action chopLog = new Action() {
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

        Action collectBranches = new Action() {
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

        ActionGraph graph = new ActionGraph();
        graph.node(chopLog);
        graph.node(collectBranches);
        graph.node(getAxe);

        Map<String, Object> baseState = new HashMap<>();
        baseState.put("axe in inventory", false);
        baseState.put("make firewood", false);
        EnvironmentInfo base = new EnvironmentInfo(null, baseState);

        Map<String, Object> goalState = new HashMap<>();
        goalState.put("make firewood", true);
        EnvironmentInfo goal = new EnvironmentInfo(null, goalState);
        Stack<ActionGraph.Node> plan = graph.plan(base, goal);
        Assert.assertEquals(chopLog, plan.pop().getAction());
        Assert.assertEquals(getAxe, plan.pop().getAction());
    }

    @Test
    public void finiteStateMachine() {
        Action getAxe = new Action() {
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

        Action chopLog = new Action() {
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

        Action collectBranches = new Action() {
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

        ActionGraph graph = new ActionGraph();
        graph.node(chopLog);
        graph.node(collectBranches);
        graph.node(getAxe);

        Map<String, Object> baseState = new HashMap<>();
        baseState.put("axe in inventory", false);
        baseState.put("make firewood", false);
        EnvironmentInfo base = new EnvironmentInfo(null, baseState);

        Map<String, Object> goalState = new HashMap<>();
        goalState.put("make firewood", true);
        EnvironmentInfo goal = new EnvironmentInfo(null, goalState);

        Agent agent = new Agent(graph);
        agent.setCurrentGoal(goal);

        Assert.assertEquals((FiniteStateMachine.State)agent::idleState, agent.getFiniteStateMachine().peekState());
        agent.step();
    }
}