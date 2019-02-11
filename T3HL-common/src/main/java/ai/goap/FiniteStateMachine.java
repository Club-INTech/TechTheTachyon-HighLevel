package ai.goap;

import java.util.Stack;

/**
 * Automate très simpliste fait pour le GOAP.
 * Uniquement trois états:
 * - Idle: l'agent n'a rien à faire et planifie ses prochaines actions
 * - Moving: l'agent est en train de se déplacer
 * - Performing: l'agent est en train de faire une action
 */
public class FiniteStateMachine {

    public interface State {
        void update(FiniteStateMachine fsm, EnvironmentInfo info);
    }

    /**
     * Pile représentant les états à executer à la suite (le but final est normalement au fond de la pile)
     */
    private Stack<State> states;

    public FiniteStateMachine() {
        states = new Stack<>();
    }

    /**
     * Avance d'un cran dans la planification: vérification de la position, replanification, etc.
     * @param info les informations sur l'environnement de l'agent
     */
    public void step(EnvironmentInfo info) {
        // seul l'état en haut de la pile doit être mis à jour
        // il peut éventuellement ajouter de nouveaux états (eg un déplacement)
        if(states.peek() != null) {
            states.peek().update(this, info);
        }
    }

    public void pushState(State state) {
        states.push(state);
    }

    public State popState() {
        return states.pop();
    }

    public State peekState() {
        return states.peek();
    }
}
