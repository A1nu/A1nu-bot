package ee.a1nu.application.database.service;

import discord4j.common.util.Snowflake;
import ee.a1nu.application.bot.core.data.CommandCategory;
import ee.a1nu.application.commandConstants.CommandAlias;
import ee.a1nu.application.database.entity.CurrencyCommand;
import ee.a1nu.application.database.repository.CurrencyCommandRepository;
import ee.a1nu.application.database.repository.GuildEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.util.Loggers;

@Service
public class CurrencyCommandService {
    private static final reactor.util.Logger log = Loggers.getLogger(CurrencyCommandService.class);
    private final CurrencyCommandRepository currencyCommandRepository;
    private final GuildEntityRepository guildEntityRepository;

    @Autowired
    public CurrencyCommandService(CurrencyCommandRepository currencyCommandRepository, GuildEntityRepository guildEntityRepository) {
        this.currencyCommandRepository = currencyCommandRepository;
        this.guildEntityRepository = guildEntityRepository;
    }

    public CurrencyCommand getOrCreateCurrencyCommandForGuild(Snowflake guildSnowflake) {
        if (isCurrencyCommandExistsForGuild(guildSnowflake)) {
            return getCurrencyCommandForGuild(guildSnowflake);
        } else {
            log.info("Creating new currency command for guild with id: %s", guildSnowflake.asString());
            return createCurrencyCommand(guildSnowflake);
        }
    }

    public boolean isCurrencyCommandExistsForGuild(Snowflake guildSnowflake) {
        return currencyCommandRepository.existsCurrencyCommandByGuild_Id(guildSnowflake.asLong());
    }

    public CurrencyCommand getCurrencyCommandForGuild(Snowflake guildSnowflake) {
        return currencyCommandRepository.findCurrencyCommandByGuild_Id(guildSnowflake.asLong());
    }

    public CurrencyCommand createCurrencyCommand(Snowflake guildSnowflake) {
        return currencyCommandRepository.save(
                CurrencyCommand
                    .builder()
                    .isEnabled(false)
                    .alias(CommandAlias.CURRENCY)
                    .category(CommandCategory.CURRENCY)
                    .guild(guildEntityRepository.getOne(guildSnowflake.asLong()))
                    .build()
        );
    }

    public CurrencyCommand update(CurrencyCommand currencyCommand) {
        CurrencyCommand existingCurrencyCommand = currencyCommandRepository.getOne(currencyCommand.getId());
        existingCurrencyCommand.setEnabled(currencyCommand.isEnabled());
        existingCurrencyCommand.setTransactionEnabled(currencyCommand.isTransactionEnabled());
        return currencyCommandRepository.save(existingCurrencyCommand);
    }
}
