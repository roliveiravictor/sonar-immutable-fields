package enumerator;

/**
 *
 * @author victor.rocha
 */
public enum AccessModifiers {
    PRIVATE("private"),
    PROTECTED("protected"),
    PUBLIC("public"),
    FINAL("final");

    private String name;

    public String getName() {
        return name;
    }

    private AccessModifiers(String name) {
        this.name = name;
    }
}
