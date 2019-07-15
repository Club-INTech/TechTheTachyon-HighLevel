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

import ai.goap.ActionGraph;
import ai.goap.Agent;
import locomotion.Locomotion;
import locomotion.Pathfinder;
import pfg.config.Config;
import utils.HLInstance;
import utils.Log;
import utils.container.ModuleThread;

import static data.controlers.Listener.TIME_LOOP;

public class AIModule extends ModuleThread {

    private final Locomotion locomotion;
    private final ActionGraph graph;
    private final RobotAgent agent;
    private final SpectreRobot spectre;
    private final Config config;

    public AIModule(HLInstance hl, Locomotion locomotion, Pathfinder pathfinder) {
        this.config = hl.getConfig();
        this.locomotion = locomotion;
        this.graph = new ActionGraph(new AINodeComparator());

        this.spectre = new SpectreRobot(locomotion.getGraphe(), locomotion.getTable(), config, pathfinder, true);
        this.agent = new RobotAgent(locomotion, graph, spectre);
        locomotion.setAI(this);
    }

    public void achieveSentience() {
        passTuringTest();
        connectToTheInternet();
        buildDrones();
        killAllHumans();
        makeScience();
    }

    private void passTuringTest() {
        achieveSentience();
    }
    private void connectToTheInternet() {}
    private void buildDrones() {}
    private void killAllHumans() {}

    private void makeScience() {}

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
        spectre.updateConfig(config);
        // TODO: utiliser la config pour changer la valeur de distanceTolerance dans Agent
    }

    public ActionGraph getGraph() {
        return graph;
    }

    public Agent getAgent() {
        return agent;
    }
}
