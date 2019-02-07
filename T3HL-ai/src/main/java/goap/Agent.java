package goap;

import java.util.Stack;

public class Agent {

    private final FiniteStateMachine fsm;

    /**
     * Le graphe des actions possibles pour cet agent
     */
    private final ActionGraph graph;

    /**
     * Pile des actions à exécuter, s'il y en a
     */
    private Stack<ActionGraph.Node> currentPlan;
    // TODO

    public Agent(ActionGraph graph) {
        this.currentPlan = null;
        this.graph = graph;
        this.fsm = new FiniteStateMachine();
        fsm.pushState(this::idleState); // on commence dans l'état Idle: on va planifier nos actions dès que possible
    }

    /**
     * Etat Idle: on réflechit aux prochaines actions
     */
    private void idleState(FiniteStateMachine fsm, EnvironmentInfo info) {
       Stack<ActionGraph.Node> plan = graph.plan(info);
       if(plan != null) { // on a un plan! \o/
           fsm.popState(); // on retire l'état idle courant
           this.currentPlan = plan;
           fsm.pushState(this::performingState);
       } else { // pas de plan, on continue de réfléchir
           ;
       }
    }

    /**
     * Etat Moving: l'agent est en train de se déplacer
     */
    private void movingState(FiniteStateMachine fsm, EnvironmentInfo info) {
        // TODO
    }

    /**
     * Etat Performing: une action est en cours
     */
    private void performingState(FiniteStateMachine fsm, EnvironmentInfo info) {
        // TODO
    }

    public void step() {
        // TODO: récupérer les infos sur l'environnement puis utiliser le graphe pour planifier
        EnvironmentInfo info = gatherEnvironmentInformation();
        fsm.step(info);
    }

    private EnvironmentInfo gatherEnvironmentInformation() {
        return null; // TODO
    }
}
