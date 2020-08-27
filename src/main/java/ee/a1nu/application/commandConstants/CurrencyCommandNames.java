package ee.a1nu.application.commandConstants;

import ee.a1nu.application.bot.core.data.CommandPermission;

import java.util.List;

public class CurrencyCommandNames {    
    public static List<CommandName> getCommandNames() {
        return List.of(
                CommandName.build(
                        "add",
                        CommandPermission.ADMIN,
                        List.of(
                                Argument.build("userName", true, "user to affect"),
                                Argument.build("amountOfCurrency", true, "amount of currency to add")
                        ),
                        "Removing listed amount of currency to user"
                ),
                CommandName.build(
                        "remove",
                        CommandPermission.ADMIN,
                        List.of(
                                Argument.build("userName", true, "user to affect"),
                                Argument.build("amountOfCurrency", true, "amount of currency to remove")
                                ),
                        "Adding listed amount of currency to user"
                ),
                CommandName.build(
                        "transfer",
                        CommandPermission.USER,
                        List.of(
                                Argument.build("userName", true, "user to affect"),
                                Argument.build("amountOfCurrency", true, "amount of currency to transfer")
                        ),
                        "transferring currency from user to user"
                ),
                CommandName.build(
                        "balance",
                        CommandPermission.USER,
                        List.of(),
                        "Get amount of current user currency"
                )
        );
    }
}
