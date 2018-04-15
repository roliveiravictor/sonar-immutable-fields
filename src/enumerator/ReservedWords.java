package enumerator;

/**
 *
 * @author victor.rocha
 */
public enum ReservedWords {
    ELEMENT("@Element"),
    SERIALIZED_NAME("@SerializedName"),
    COLOR_INT("@ColorInt"),
    SETTER("set"),
    SESSION_CONTROLLER_ACTIVITY("mActivity"),
    IMPORT("import"),
    
    STORE_ID("id"),
    STORE_MENU("menu"),
    STORE_CRM("cardCrm"),
    STORE_BAR("navigationBarText"),
    STORE_TILE("titleText"),
    STORE_TITLE_COLOR("titleColor"),
    STORE_BODY("bodyText"),
    STORE_TEXT_COLOR("textColor"),
    STORE_BACKGROUND("backgroundColor"),
    STORE_CAMPAIGNS("seasonalCampaigns");


    private String name;

    public String getName() {
        return name;
    }

    private ReservedWords(String name) {
        this.name = name;
    }

}
