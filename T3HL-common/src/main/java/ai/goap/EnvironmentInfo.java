package ai.goap;

import data.XYO;
import utils.math.Vec2;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant toutes les informations sur l'environnement d'un agent. Utilisé par les actions et les états pour savoir ce qu'ils doivent faire
 */
public class EnvironmentInfo {
    // TODO

    private final Map<String, Object> state;
    private final XYO xyo;

    public EnvironmentInfo(XYO xyo, Map<String, Object> state) {
        this.state = state;
        this.xyo = xyo;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public XYO getXYO() {
        return xyo;
    }

    public Vec2 getCurrentPosition() {
        return xyo.getPosition();
    }

    public double getCurrentAngle() {
        return xyo.getOrientation();
    }

    public EnvironmentInfo copyWithEffects(Map<String, Object> effects) {
        Map<String, Object> newState = new HashMap<>(state);
        newState.putAll(effects);
        return new EnvironmentInfo(xyo, newState);
    }

    public boolean isMetByState(EnvironmentInfo other) {
        for (Map.Entry<String, Object> entry : state.entrySet()) {
            if(!other.state.get(entry.getKey()).equals(entry.getValue()))
                return false;
        }
        return true;
    }
}
