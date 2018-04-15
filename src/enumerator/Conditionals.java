package enumerator;

/**
 *
 * @author victor.rocha
 */
public enum Conditionals {
    IF("if"),
    SWITCH("switch");
    
    private String name;

    public String getName() {
        return name;
    }

    private Conditionals(String name) {
        this.name = name;
    }
}
