package ee.a1nu.application.database.service;

import discord4j.common.util.Snowflake;
import ee.a1nu.application.database.entity.Guild;
import ee.a1nu.application.database.repository.GuildEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.util.Loggers;

@Service("guildEntityService")
public class GuildEntityService {
    private static final reactor.util.Logger log = Loggers.getLogger(GuildEntityService.class);
    private GuildEntityRepository guildEntityRepository;

    @Autowired
    public GuildEntityService(GuildEntityRepository guildEntityRepository) {
        this.guildEntityRepository = guildEntityRepository;
    }

    public Guild getOrCreateGuild(Snowflake snowflake) {
        if (isGuildExists(snowflake)) {
            return getGuild(snowflake);
        } else {
            log.info(String.format("Creating new guild with id: %s", snowflake.asString()));
            return createGuild(snowflake);
        }
    }

    public void createIfNotExists(Snowflake guildSnowflake) {
        if (!isGuildExists(guildSnowflake)) {
            createGuild(guildSnowflake);
        }
    }

    public Boolean isGuildExists(Snowflake snowflake) {
        return guildEntityRepository.existsGuildById(snowflake.asLong());
    }

    public Guild getGuild(Snowflake snowflake) {
        return guildEntityRepository.getOne(snowflake.asLong());
    }

    public Guild createGuild(Snowflake snowflake) {
        return guildEntityRepository.save(Guild.builder().id(snowflake.asLong()).premium(false).build());
    }
}
