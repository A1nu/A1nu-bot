package ee.a1nu.application.commandConstants;

import ee.a1nu.application.bot.core.data.CommandPermission;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommandName {
    String commandName;
    CommandPermission commandNamePermission;
    List<Argument> arguments;
    String description;

    public static CommandName build(String commandName, CommandPermission commandNamePermission, List<Argument> arguments, String description) {
        return CommandName
                .builder()
                .commandName(commandName)
                .commandNamePermission(commandNamePermission)
                .arguments(arguments)
                .description(description)
                .build();
    }
}
