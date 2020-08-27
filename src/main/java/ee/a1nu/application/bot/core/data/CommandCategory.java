package ee.a1nu.application.bot.core.data;

import java.util.HashMap;
import java.util.Map;

public enum CommandCategory {

//    UTILS("Utility"),
    CURRENCY("Currency");
//    ADMIN("Admin");

    private final String name;

    CommandCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static Map<String, CommandCategory> getMap() {
        Map<String, CommandCategory> map = new HashMap<String, CommandCategory>();
        for (CommandCategory commandCategory : CommandCategory.values()) {
            map.put(commandCategory.getName(), commandCategory);
        }
        return map;
    }
}
