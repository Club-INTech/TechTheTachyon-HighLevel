package ai.goap;

import utils.Log;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Agent {

    private double distanceTolerance = 10.0; // TODO

    private final FiniteStateMachine fsm;

    /**
     * Le graphe des actions possibles pour cet agent
     */
    private final ActionGraph graph;

    /**
     * Pile des actions à exécuter, s'il y en a
     */
    private Stack<ActionGraph.Node> currentPlan;

    private Vec2 targetPosition = new VectCartesian(0.0f, 0.0f);
    private Vec2 previousTargetPosition = new VectCartesian(0.0f, 0.0f);

    /**
     * Liste des erreurs dans le mouvement, mis à jour via #reportMovementError
     */
    private final List<Exception> movementErrors = new LinkedList<>();
    private EnvironmentInfo currentGoal;
    // TODO: plus de log
    // TODO: interface avec le reste du HL

    public Agent(ActionGraph graph) {
        this.currentPlan = null;
        this.graph = graph;
        this.fsm = new FiniteStateMachine();
        fsm.pushState(this::idleState); // on commence dans l'état Idle: on va planifier nos actions dès que possible
    }

    /**
     * Etat Idle: on réflechit aux prochaines actions
     */
    public void idleState(FiniteStateMachine fsm, EnvironmentInfo info) {
        if(currentGoal == null) {
            Log.AI.critical("Tentative de planification alors qu'il n'y a pas de but donné!");
            return;
        }
        Stack<ActionGraph.Node> plan = graph.plan(info, currentGoal);
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
    public void movingState(FiniteStateMachine fsm, EnvironmentInfo info) {
        if(info.getCurrentPosition().distanceTo(targetPosition) < distanceTolerance) {
            fsm.popState(); // on a fini le mouvement, on passe à l'état d'après
        } else { // on a toujours pas atteint la position
            synchronized (movementErrors) {
                if(!movementErrors.isEmpty()) { // on a eu un problème dans le mouvement, on arrête le mouvement et on reréfléchi
                    Log.AI.critical("Impossible de finir le mouvement");
                    Log.AI.debug("[=== Erreur dans le mouvement à cause des erreurs suivantes: ===]");
                    for(Exception error : movementErrors) {
                        Log.AI.debug("\t-"+error);
                    }
                    fsm.popState();
                    movementErrors.clear();
                } else {
                    tryToMoveTo(targetPosition);
                }
            }
        }
    }

    private void tryToMoveTo(Vec2 position) {
        if(previousTargetPosition.distanceTo(position) > distanceTolerance) { // nouvelle position!
            Log.AI.debug("Envoi de l'ordre de déplacement vers la position "+position);
            // TODO: envoyer l'ordre de mouvement
        }
        previousTargetPosition.setXY(position.getX(), position.getY());
    }

    /**
     * Etat Performing: une action est en cours
     */
    public void performingState(FiniteStateMachine fsm, EnvironmentInfo info) {
        if(currentPlan != null && !currentPlan.isEmpty()) {
            ActionGraph.Node currentAction = currentPlan.peek();
            if(currentAction.checkCompletion(info)) {
                // on applique les effets de cette action
                Map<String, Object> effects = currentAction.getAction().getEffects();
                info.getState().putAll(effects);
                currentPlan.pop(); // on retire l'action qui a fini
            } else {
                if(currentAction.requiresMovement(info)) {
                    currentAction.updateTargetPosition(info, targetPosition);
                    fsm.pushState(this::movingState);
                } else {
                    currentAction.performAction(info);
                }
            }
        } else {
            fsm.popState();
            fsm.pushState(this::idleState); // on retourne réfléchir
        }
    }

    public void step() {
        EnvironmentInfo info = gatherEnvironmentInformation();
        fsm.step(info);
    }

    private EnvironmentInfo gatherEnvironmentInformation() {
        return null; // TODO
    }

    /**
     * Permet de faire savoir à l'agent que le mouvement pose problème
     */
    public void reportMovementError(Exception error) {
        synchronized (movementErrors) {
            this.movementErrors.add(error);
        }
    }

    public double getDistanceTolerance() {
        return distanceTolerance;
    }

    public void setDistanceTolerance(double distanceTolerance) {
        this.distanceTolerance = distanceTolerance;
    }

    public Stack<ActionGraph.Node> getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentGoal(EnvironmentInfo currentGoal) {
        this.currentGoal = currentGoal;
    }

    public FiniteStateMachine getFiniteStateMachine() {
        return fsm;
    }
}
