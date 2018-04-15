package enumerator;

/**
 *
 * @author victor.rocha
 */
public enum Patterns {
    LEFT_BRACKET("{"),
    BREAKPOINT(";"),
    COMMA(","),
    POINT("."),
    PARAMETER_WITH_THROWS(") throws"),
    PARAMETER_WITH_SPACES(") {"),
    PARAMETER_WITHOUT_SPACES("){"),
    INCREMENT_PP_WITHOUT_SPACES("++"),
    INCREMENT_PP_WITH_SPACES(" ++ "),
    INCREMENT_PE_WITHOUT_SPACES("+="),
    INCREMENT_PE_WITH_SPACES(" += "),
    DECREMENT_PP_WITHOUT_SPACES("--"),
    DECREMENT_PP_WITH_SPACES(" -- "),
    DECREMENT_PE_WITHOUT_SPACES("-="),
    DECREMENT_PE_WITH_SPACES(" -= "),
    TAB("\t"),
    COMMENT("//"),
    EMPTY_STRING(""),
    NULL_SET(" = null"),
    EMPTY_SET(" = \"\""),
    DUMMY_INT(" = 0;"),
    DUMMY_BOOLEAN(" = false;"),
    DUMMY_OBJECT_INITIALIZER(" = null;"),
    FIELD_ATTRIBUTION_WITH_SPACES(" = "),
    FIELD_ATTRIBUTION_WITHOUT_SPACES("="),
    EQUALS_ASSERT_WITHOUT_SPACES("=="),
    EQUALS_ASSERT_WITH_SPACES(" == "),
    WHITE_SPACE(" "), 
    BRACKETS_REGEX("\\[\\]"),
    DOUBLE_LEFT_PARENTHESES_REGEX("(("),
    DOUBLE_RIGHT_PARENTHESES_STRING("))"),
    BRACKETS_STRING("[]"),
    NON_ALPHABETICAL_CHARACTERS("[^a-zA-Z]"),
    THIS("this."),
    INJECTION("@");



    private String name;

    public String getName() {
        return name;
    }

    private Patterns(String name) {
        this.name = name;
    }
}
