package enumerator;

/**
 *
 * @author victor.rocha
 */
public enum Loops {
    FOR("for"),
    WHILE("while");
    
    private String name;

    public String getName() {
        return name;
    }

    private Loops(String name) {
        this.name = name;
    }
}
