package lowlevel.order;

import utils.RobotSide;

/**
 * Ordre simple créé par un {@link OrderBuilder}
 *
 * @author jglrxavpok
 */
class BuiltSidedOrder implements SidedOrder {

    private final String system;
    private final RobotSide side;
    private final String terminal;
    private BuiltSidedOrder symetrized;

    BuiltSidedOrder(String system, RobotSide side, String terminal) {
        this.system = system;
        this.side = side;
        this.terminal = terminal;
    }

    @Override
    public SidedOrder createSymetrized() {
        return new BuiltSidedOrder(system, side().opposite(), terminal);
    }

    @Override
    public RobotSide side() {
        return side;
    }

    @Override
    public SidedOrder symetrize() {
        return symetrized;
    }

    @Override
    public String toLL() {
        return system+" "+side.toString()+" "+terminal;
    }

    /**
     * Permets de garder l'instance de l'ordre symétrisé pour éviter de le calculer à chaque appel de symetrize
     * @param symetrized
     */
    void setSymetrized(BuiltSidedOrder symetrized) {
        this.symetrized = symetrized;
    }
}
