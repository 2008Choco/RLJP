package wtf.choco.rljp.structures.properties;

/**
 * A null {@link Property}, a.k.a. a property with no value.
 */
public final class NullProperty extends Property {

    /**
     * A shared null property instance.
     */
    public static final NullProperty INSTANCE = new NullProperty();

    private NullProperty() {
        super("None", PropertyType.NULL, 0, 0);
    }

    @Override
    protected String stringifyData() {
        return ""; // NullProperty has no data
    }

}
