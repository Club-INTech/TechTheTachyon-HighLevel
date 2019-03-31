package ai;

import ai.goap.Action;
import ai.goap.EnvironmentInfo;
import utils.math.Vec2;

public class MoveToPointAction extends Action {

    protected final Vec2 aim;
    protected boolean executed;

    public MoveToPointAction(Vec2 aim) {
        this.aim = aim;
    }

    @Override
    public double getCost(EnvironmentInfo info) {
        return info.getXYO().getPosition().squaredDistanceTo(aim) + Math.abs(info.getXYO().getPosition().angleTo(aim));
    }

    @Override
    public void perform(EnvironmentInfo info) {
        executed = true;
    }

    @Override
    public boolean isComplete(EnvironmentInfo info) {
        return executed;
    }

    @Override
    public boolean arePreconditionsMet(EnvironmentInfo info) {
        return !executed && super.arePreconditionsMet(info);
    }

    @Override
    public boolean requiresMovement(EnvironmentInfo info) {
        return true;
    }

    @Override
    public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {
        targetPos.set(aim);
    }

    @Override
    public void reset() {

    }
}
