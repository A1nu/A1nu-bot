package ee.a1nu.application.web.controller;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import ee.a1nu.application.bot.Bot;
import ee.a1nu.application.web.dto.GuildPOJO;
import ee.a1nu.application.web.rest.DiscordRestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    private final Environment env;
    private final DiscordRestService discordRestService;
    private final Bot bot;

    @Autowired
    public DashboardController(Environment env, Bot bot, DiscordRestService discordRestService) {
        this.env = env;
        this.bot = bot;
        this.discordRestService = discordRestService;
    }

    @GetMapping(value="/dashboard")
    public ModelAndView dashboard(@RegisteredOAuth2AuthorizedClient("discord") OAuth2AuthorizedClient authorizedClient) {
        ModelAndView modelAndView = new ModelAndView();
        List<GuildPOJO> guildList = discordRestService.getUserGuilds(authorizedClient);
        List<Guild> botGuilds = bot.getBotGuilds();
        List<GuildPOJO> guildsWhereUserHasAdminAuthority =
                guildList
                        .stream()
                        .peek(guildPOJO -> guildPOJO.setBotExists(botGuilds.stream().anyMatch(guild -> guild.getId().equals(Snowflake.of(Long.parseLong(guildPOJO.getId()))))))
                        .filter(guildPOJO -> guildPOJO.getBotExists() || guildPOJO.getOwner() || guildPOJO.getPermissions() == 2147483647)
                        .sorted(Comparator.comparing(GuildPOJO::getName))
                        .sorted(Comparator.comparing(GuildPOJO::getOwner).reversed())
                        .collect(Collectors.toList());
        modelAndView.addObject("administrationGuilds", guildsWhereUserHasAdminAuthority);
        modelAndView.addObject("client_id", env.getProperty("spring.security.oauth2.client.registration.discord.client-id"));
        modelAndView.setViewName("view/dashboard");
        return modelAndView;
    }
}
