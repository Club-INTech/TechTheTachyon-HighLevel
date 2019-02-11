package ai;

import ai.goap.Action;
import ai.goap.ActionGraph;
import ai.goap.Agent;
import ai.goap.EnvironmentInfo;
import data.XYO;
import locomotion.Locomotion;
import pfg.config.Config;
import utils.Log;
import utils.container.Service;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.HashMap;
import java.util.Map;

import static data.controlers.Listener.TIME_LOOP;

public class AIService extends Thread implements Service {

    private final Locomotion locomotion;
    private final ActionGraph graph;
    private final RobotAgent agent;

    public AIService(Locomotion locomotion) {
        this.locomotion = locomotion;
        this.graph = new ActionGraph();
        this.agent = new RobotAgent(locomotion, graph);
    }

    @Override
    public void run() {
        Log.AI.debug("Starting AI service");
        while (!Thread.currentThread().isInterrupted()) {
            agent.step();

            try {
                Thread.sleep(TIME_LOOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateConfig(Config config) {
        // TODO: utiliser la config pour changer la valeur de distanceTolerance dans Agent
    }

    public ActionGraph getGraph() {
        return graph;
    }

    public Agent getAgent() {
        return agent;
    }
}
