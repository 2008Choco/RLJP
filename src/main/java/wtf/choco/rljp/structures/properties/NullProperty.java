package wtf.choco.rljp.structures.properties;

public final class NullProperty extends Property {

    public static final NullProperty INSTANCE = new NullProperty();

    private NullProperty() {
        super("None", PropertyType.NULL, 0, 0);
    }

    @Override
    protected String stringifyData() {
        return ""; // NullProperty has no data
    }

}
