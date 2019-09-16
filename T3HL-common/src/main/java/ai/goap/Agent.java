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

package ai.goap;

import utils.Log;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public abstract class Agent {

    private double squaredDistanceTolerancy = 10.0;

    private final FiniteStateMachine fsm;

    /**
     * Le graphe des actions possibles pour cet agent
     */
    private final ActionGraph graph;

    /**
     * Pile des actions à exécuter, s'il y en a
     */
    private Stack<ActionGraph.Node> currentPlan;

    private Vec2 targetPosition = new InternalVectCartesian(0, 0);
    private Vec2 previousTargetPosition = new InternalVectCartesian(0, 0);

    /**
     * Liste des erreurs dans le mouvement, mis à jour via #reportMovementError
     */
    private final List<Exception> movementErrors = new LinkedList<>();
    private EnvironmentInfo currentGoal;

    // ce sont des champs parce qu'il faut pouvoir avoir la même référence pour tester
    private final FiniteStateMachine.State idleState = new FiniteStateMachine.State() {
        @Override
        public void update(FiniteStateMachine fsm, EnvironmentInfo info) {
            updateIdleState(fsm, info);
        }

        @Override
        public String toString() {
            return "Idle";
        }
    };
    private final FiniteStateMachine.State movingState = new FiniteStateMachine.State() {
        @Override
        public void update(FiniteStateMachine fsm, EnvironmentInfo info) {
            updateMovingState(fsm, info);
        }

        @Override
        public String toString() {
            return "Moving";
        }
    };
    private final FiniteStateMachine.State performingState = new FiniteStateMachine.State() {
        @Override
        public void update(FiniteStateMachine fsm, EnvironmentInfo info) {
            updatePerformingState(fsm, info);
        }

        @Override
        public String toString() {
            String action = "<NO PLAN>";
            if(currentPlan != null && !currentPlan.isEmpty()) {
                action = currentPlan.peek().getAction().toString();
            }
            return "Performing "+action;
        }
    };
    // TODO: plus de log?

    public Agent(ActionGraph graph) {
        this.currentPlan = null;
        this.graph = graph;
        this.fsm = new FiniteStateMachine();
        fsm.pushState(this.idleState); // on commence dans l'état Idle: on va planifier nos actions dès que possible
    }

    protected abstract EnvironmentInfo gatherEnvironmentInformation();
    protected abstract void orderMove(Vec2 position);

    protected void onPlanUpdated(EnvironmentInfo info) {

    }

    /**
     * Etat Idle: on réflechit aux prochaines actions
     */
    private void updateIdleState(FiniteStateMachine fsm, EnvironmentInfo info) {
        if(currentGoal == null) {
            Log.AI.critical("Tentative de planification alors qu'il n'y a pas de but donné!");
            return;
        }
        if(currentPlan != null && !currentPlan.isEmpty()) {
            fsm.popState();
            fsm.pushState(this.performingState);
            return;
        }
        long startTime = System.currentTimeMillis();

        // on copie les infos sur l'environnement pour être sûr de rien changer
        EnvironmentInfo envCopy = info.copyWithEffects(graph.getCopyAction());
        Stack<ActionGraph.Node> plan = graph.plan(envCopy, currentGoal);
        long elapsed = System.currentTimeMillis()-startTime;
        Log.AI.debug("Planning took "+elapsed+"ms ("+elapsed/1000.0+"s)");
        if(plan != null) { // on a un plan! \o/
           fsm.popState(); // on retire l'état idle courant
           this.currentPlan = plan;
           fsm.pushState(this.performingState);
           onPlanUpdated(info);
        } else { // pas de plan, on continue de réfléchir
           ;
        }
    }

    /**
     * Etat Moving: l'agent est en train de se déplacer
     */
    private void updateMovingState(FiniteStateMachine fsm, EnvironmentInfo info) {
        Log.AI.debug("MOVING");
        if(info.getCurrentPosition().squaredDistanceTo(targetPosition) <= squaredDistanceTolerancy) {
            Log.AI.debug("MOVING pop");
            fsm.popState(); // on a fini le mouvement, on passe à l'état d'après
        } else { // on a toujours pas atteint la position
            synchronized (movementErrors) {
                if(!movementErrors.isEmpty()) { // on a eu un problème dans le mouvement, on arrête le mouvement et on reréfléchi
                    Log.AI.critical("Impossible de finir le mouvement");
                    Log.AI.debug("Erreur dans le mouvement à cause des erreurs suivantes:");
                    for(Exception error : movementErrors) {
                        Log.AI.debug("\t- "+error);
                    }
                    fsm.popState();
                    movementErrors.clear();
                } else {
                    Log.AI.debug("try to move "+previousTargetPosition+" / "+targetPosition+" / "+info.getCurrentPosition());
                    tryToMoveTo(targetPosition);
                }
            }
        }
    }

    private void tryToMoveTo(Vec2 position) {
      //  if(previousTargetPosition.squaredDistanceTo(position) > squaredDistanceTolerancy) { // nouvelle position!
            Log.AI.debug("Envoi de l'ordre de déplacement vers la position "+position);
            orderMove(position.clone());
            previousTargetPosition.setXY(position.getX(), position.getY());
       // }
    }

    /**
     * Etat Performing: une action est en cours
     */
    private void updatePerformingState(FiniteStateMachine fsm, EnvironmentInfo info) {
        if(currentPlan != null && !currentPlan.isEmpty()) {
            ActionGraph.Node currentAction = currentPlan.peek();
            Log.AI.debug("Potentially Performing "+currentAction.getAction());
            if(currentAction.requiresMovement(info)) {
                currentAction.updateTargetPosition(info, targetPosition);
            }
            if (currentAction.requiresMovement(info) && checkNotInRange(currentAction, info)) {
                Log.AI.debug(currentAction.getAction()+" has set target pos: "+targetPosition+" last = "+this.previousTargetPosition);
                fsm.pushState(this.movingState);
            } else if (currentAction.checkCompletion(info)) {
                Log.AI.debug("Fin de "+currentAction.getAction());
                // on applique les effets de cette action
                currentAction.getAction().applyChangesToEnvironment(info);
                currentPlan.pop(); // on retire l'action qui a fini

                if (currentPlan.isEmpty()) {
                    fsm.popState();
                    fsm.pushState(this.idleState); // on retourne réfléchir
                }
            } else {
                Log.AI.debug("Performing "+currentAction.getAction());
                currentAction.performAction(info);
            }
        } else {
            fsm.popState();
            fsm.pushState(this.idleState); // on retourne réfléchir
        }
    }

    private boolean checkNotInRange(ActionGraph.Node node, EnvironmentInfo info) {
/*        Vec2 target = new VectCartesian(0,0);
        node.updateTargetPosition(info, target);*/
        boolean notInRange = info.getCurrentPosition().squaredDistanceTo(targetPosition) > squaredDistanceTolerancy;
        Log.AI.debug("Check range for action "+node.getAction()+": "+info.getCurrentPosition()+" <-> "+targetPosition+" => in range = "+!notInRange);
        return notInRange;
    }

    public void step() {
        EnvironmentInfo info = gatherEnvironmentInformation();
        fsm.step(info);
    }

    /**
     * Permet de faire savoir à l'agent que le mouvement pose problème
     */
    public void reportMovementError(Exception error) {
        synchronized (movementErrors) {
            this.movementErrors.add(error);
        }
    }

    public double getSquaredDistanceTolerancy() {
        return squaredDistanceTolerancy;
    }

    public void setSquaredDistanceTolerancy(double squaredDistanceTolerancy) {
        this.squaredDistanceTolerancy = squaredDistanceTolerancy;
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

    public FiniteStateMachine.State getIdleState() {
        return idleState;
    }

    public FiniteStateMachine.State getMovingState() {
        return movingState;
    }

    public FiniteStateMachine.State getPerformingState() {
        return performingState;
    }
}
