package ee.a1nu.application.commandConstants;

public enum CommandAlias {
    CURRENCY("currency");

    private final String name;

    CommandAlias(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}
