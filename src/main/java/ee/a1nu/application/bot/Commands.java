package ee.a1nu.application.bot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import ee.a1nu.application.Config;
import ee.a1nu.application.bot.core.data.CommandCategory;
import ee.a1nu.application.commandConstants.CommandName;
import ee.a1nu.application.commandConstants.CommandNamesService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Commands {
    private final Bot bot;
    private final CommandNamesService commandNamesService;

    @Autowired
    public Commands(Bot bot, CommandNamesService commandNamesService) {
        this.bot = bot;
        this.commandNamesService = commandNamesService;
        initializeCommands();
    }

    private void initializeCommands() {
        bot.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().startsWith(Config.BOT_PREFIX))
                .flatMap(this::processMessage)
                .subscribe();
    }

    private Publisher processMessage(Message message) {
        String[] content = message.getContent().replace(Config.BOT_PREFIX, "").split(" ");
        if (!CommandCategory.getMap().containsKey(content[0])) {
            return message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("check help on https://bot.a1nu.ee/docs"));
        }
        CommandCategory commandCategory = CommandCategory.getMap().get(content[0]);
        List<CommandName> commandNames = commandNamesService.getCommandNamesByCommandCategory(commandCategory);

        return message.getChannel().flatMap(messageChannel -> messageChannel.createMessage(String.format("you clicked command %s", content[0])));

//        switch (content.toLowerCase()) {
//            case ("ping"):
//                message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("pong")).subscribe();
//
//        }
    }


}