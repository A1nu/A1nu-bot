package ee.a1nu.application.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.discordjson.json.gateway.GuildCreate;
import discord4j.rest.util.PermissionSet;
import ee.a1nu.application.database.service.GuildEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Objects;
import java.util.List;

@Service(value = "bot")
public class Bot {
    private final GatewayDiscordClient client;
    private final GuildEntityService guildEntityService;

    private static final Logger log = Loggers.getLogger(DiscordClientBuilder.class);

    @Autowired
    public Bot(
            Environment env,
            GuildEntityService guildEntityService
    ) {
        this.guildEntityService = guildEntityService;

        client = DiscordClientBuilder.create(Objects.requireNonNull(env.getProperty("spring.bot.secret")))
                .build()
                .gateway()
                .setInitialStatus(ignore -> Presence.online(Activity.playing("~help for more info")))
                .login()
                .block();

        assert client != null;
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    log.info(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });
    }

    public void setGuildListener() {
        client.getEventDispatcher().on(GuildCreateEvent.class)
                .subscribe(event -> guildEntityService.createIfNotExists(event.getGuild().getId()));
    }

    public List<Member> getGuildMembers(Snowflake guildSnowflake) {
        return client.getGuildMembers(guildSnowflake).collectList().block();
    }

    public Member getGuildMember(Snowflake guildSnowflake, Snowflake memberSnowflake) {
        return Objects.requireNonNull(client.getGuildById(guildSnowflake).block())
                .getMemberById(memberSnowflake).block();
    }

    public List<Guild> getBotGuilds() {
        return client.getGuilds().collectList().block();
    }

    public Guild getGuild(Snowflake guildSnowflake) {
        return client.getGuildById(guildSnowflake).block();
    }

    public PermissionSet getMemberPermissions(Snowflake memberSnowflake, Snowflake guildSnowflake) {
        return Objects.requireNonNull(Objects.requireNonNull(client.getGuildById(guildSnowflake).block()).getMemberById(memberSnowflake).block()).getBasePermissions().block();
    }

    public EventDispatcher getEventDispatcher() {
        return client.getEventDispatcher();
    }
}