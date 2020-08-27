package ee.a1nu.application.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.rest.util.PermissionSet;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Collections;
import java.util.Objects;
import java.util.List;

@Service(value = "bot")
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
    }
    public GatewayDiscordClient getClient() {
        return client;
    }

    public List<Member> getGuildMembers(Snowflake guildSnowflake) {
        return client.getGuildMembers(guildSnowflake).collectList().block();
    }

    public Member getGuildMember(Snowflake guildSnowflake, Snowflake memberSnowflake) {
        return Objects.requireNonNull(client.getGuildById(guildSnowflake).block())
                .getMemberById(memberSnowflake).block();
    }

    public PermissionSet getMemberPermissions(Snowflake memberSnowflake, Snowflake guildSnowflake) {
        return Objects.requireNonNull(Objects.requireNonNull(client.getGuildById(guildSnowflake).block()).getMemberById(memberSnowflake).block()).getBasePermissions().block();
    }
}