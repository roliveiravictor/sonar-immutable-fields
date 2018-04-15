package enumerator;

/**
 *
 * @author victor.rocha
 */
public enum PrimitiveTypes {
    BYTE("byte"),
    SHORT("short"),
    INT("int"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    CHAR("char"),
    STRING("String"),
    BOOLEAN("boolean");
    
    private String name;

    public String getName() {
        return name;
    }

    private PrimitiveTypes(String name) {
        this.name = name;
    }
}
