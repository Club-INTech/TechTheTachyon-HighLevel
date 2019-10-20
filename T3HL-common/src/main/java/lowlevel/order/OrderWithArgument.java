package lowlevel.order;

import java.util.Locale;

/**
 * Ordre qui accepte des arguments
 *
 * @author jglrxavpok
 */
public class OrderWithArgument implements Order {

    private final String[] types;
    private StringBuilder builder = new StringBuilder();
    private String base;

    public OrderWithArgument(String base, String... types) {
        this.base = base;
        this.types = types;
    }

    /**
     * Génère l'ordre envoyé au LL avec les arguments donnés.
     * @param arguments la liste des arguments à appliquer à l'ordre
     * @return l'ordre à envoyer au LL avec les arguments insérés
     */
    public String with(Object... arguments) {
        if(arguments.length != types.length)
            throw new IllegalArgumentException("Required argument count ("+types.length+") is not the actual argument count ("+arguments.length+")");
        builder.setLength(0);
        builder.append(base);
        for (String type : types) {
            builder.append(" ");
            builder.append(type);
        }
        return String.format(Locale.US, builder.toString(), arguments);
    }

    /**
     * Génère une instance d'Order avec les arguments donnés
     * @param arguments la liste des arguments à appliquer à l'ordre
     * @return l'instance d'Order correspondant aux arguments donnés. Attention: cette instance n'est pas modifiable
     */
    public Order compileWith(Object... arguments) {
        String llOrder = with(arguments);
        return () -> llOrder;
    }

    @Override
    public String toLL() {
        throw new UnsupportedOperationException("You need to use #with to input arguments in order to use this order.");
    }

    public String getBase() {
        return base;
    }
}
