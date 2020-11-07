package ee.a1nu.application.commandConstants;

import ee.a1nu.application.bot.core.data.CommandCategory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommandNamesService {
    Map<CommandCategory, List<CommandName>> commandCategoryListMap;

    public CommandNamesService() {
        commandCategoryListMap = new HashMap<>();
        commandCategoryListMap.put(CommandCategory.CURRENCY, CurrencyCommandNames.getCommandNames());
    }

    public List<CommandName> getCommandNamesByCommandCategory(CommandCategory commandCategory) {
        return commandCategoryListMap.get(commandCategory);
    }

    public Map<CommandCategory, List<CommandName>> getAllCategoriesWithCommand() {
        return commandCategoryListMap;
    }
}
