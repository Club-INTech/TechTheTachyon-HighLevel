package ai.goap;

import utils.Log;
import utils.math.Vec2;

import java.util.HashMap;
import java.util.Map;

public abstract class Action {

    protected final Map<String, Object> preconditions = new HashMap<>();
    protected final Map<String, Object> effects = new HashMap<>();

    public Action() {
    }

    /**
     * Est-ce que les préconditions pour cette action sont remplies ? NE PREND PAS EN COMPTE LA POSITION ACTUELLE
     */
    public boolean arePreconditionsMet(EnvironmentInfo info) {
        for(Map.Entry<String, Object> entry : preconditions.entrySet()) {
            Object requiredState = info.getState().get(entry.getKey());
            if(requiredState == null && entry.getValue() != null) {
                Log.AI.debug("failed to meet precondition: "+entry.getKey());
                return false;
            }
            if( ! requiredState.equals(entry.getValue())) { // une condition n'est pas remplie
                Log.AI.debug("failed to meet precondition: "+entry.getKey());
                return false;
            }
        }
        return true;
    }

    public Map<String, Object> getEffects() {
        return effects;
    }

    /**
     * Coût de l'action, peut être dynamique!
     */
    public abstract double getCost(EnvironmentInfo info);

    /**
     * Le coeur de l'action: qu'est-ce qu'on fait?
     */
    public abstract void perform(EnvironmentInfo info);

    /**
     * Est-ce que l'action est finie?
     */
    public abstract boolean isComplete(EnvironmentInfo info);

    /**
     * A-t-on besoin de se déplacer pour faire cette action?
     */
    public abstract boolean requiresMovement(EnvironmentInfo info);

    /**
     * Si besoin de se déplacer: il faut mettre à jour la position cible
     * @param targetPos la position cible à mettre à jour
     */
    public abstract void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos);


    public abstract void reset();
}
