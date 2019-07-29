package lowlevel.order;

import utils.RobotSide;

/**
 * Permet de faire comprendre au HL qu'un autre donné est un ordre d'ascenseur
 *
 * @author jglrxavpok
 */
public class SidedElevatorOrderWrapper implements ElevatorOrder, SidedOrder {

    private SidedOrder base;
    private SidedOrder symetrized;

    public SidedElevatorOrderWrapper(SidedOrder base) {
        this.base = base;
        this.symetrized = createSymetrized();
    }

    public SidedElevatorOrderWrapper(SidedOrder base, SidedElevatorOrderWrapper symetrized) {
        this.base = base;
        this.symetrized = symetrized;
    }

    @Override
    public String toLL() {
        return base.toLL();
    }

    @Override
    public RobotSide side() {
        return base.side();
    }

    @Override
    public SidedOrder symetrize() {
        return symetrized;
    }

    @Override
    public SidedOrder createSymetrized() {
        return new SidedElevatorOrderWrapper(base.createSymetrized(), this);
    }
}
