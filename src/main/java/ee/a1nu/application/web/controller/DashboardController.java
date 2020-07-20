package ee.a1nu.application.web.controller;

import ee.a1nu.application.web.dto.GuildPOJO;
import ee.a1nu.application.web.rest.DiscordRestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private DiscordRestService discordRestService;


    @GetMapping(value="/dashboard")
    public ModelAndView dashboard(@RegisteredOAuth2AuthorizedClient("discord") OAuth2AuthorizedClient authorizedClient){
        ModelAndView modelAndView = new ModelAndView();
        List<GuildPOJO> guildList = discordRestService.getUserGuilds(authorizedClient);

        List<GuildPOJO> guildsWhereUserHasAdminAuthority =
                guildList
                        .stream()
                        .filter(guildPOJO -> guildPOJO.isOwner() || guildPOJO.getPermissions() == 2147483647)
                        .sorted(Comparator.comparing(GuildPOJO::getName))
                        .sorted(Comparator.comparing(GuildPOJO::isOwner).reversed())
                        .collect(Collectors.toList());
        modelAndView.addObject("administrationGuilds", guildsWhereUserHasAdminAuthority);
        modelAndView.setViewName("view/dashboard");
        return modelAndView;
    }
}
