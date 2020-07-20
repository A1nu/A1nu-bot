package ee.a1nu.application.bot;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Objects;

@Service
public class Bot {
    GatewayDiscordClient client;
    private static final Logger log = Loggers.getLogger(DiscordClientBuilder.class);
    public Bot(Environment env) {
        client = DiscordClientBuilder.create(Objects.requireNonNull(env.getProperty("spring.bot.secret")))
                .build()
                .gateway()
                .setInitialStatus(ignore -> Presence.online(Activity.playing("In development")))
                .login()
                .block();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    log.info(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });

        client.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if ("!ping".equals(message.getContent())) {
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("Pong!").block();
            }
        });
    }
}