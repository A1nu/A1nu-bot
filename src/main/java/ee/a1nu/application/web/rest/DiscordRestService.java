package ee.a1nu.application.web.rest;

import ee.a1nu.application.web.dto.GuildPOJO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ee.a1nu.application.web.configuration.SecurityConfiguration.DISCORD_BOT_USER_AGENT;

@Service("discordRestService")
public class DiscordRestService {

    @Value("${discord.apiUrl}")
    private String discordApiUrl;

    public List<GuildPOJO> getUserGuilds(OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        RestTemplate restTemplate = new RestTemplate();

        String guildsUrl = discordApiUrl + "users/@me/guilds";

        HttpEntity<String> entity = new HttpEntity<>("body", buildHeaders(oAuth2AuthorizedClient));

        return Arrays.asList(Objects.requireNonNull(restTemplate.exchange(
            guildsUrl,
            HttpMethod.GET,
            entity,
            GuildPOJO[].class).getBody()));
    }

    private HttpHeaders buildHeaders(OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        HttpHeaders headers = new HttpHeaders();

        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", oAuth2AuthorizedClient.getAccessToken().getTokenValue()));
        headers.set(HttpHeaders.USER_AGENT, DISCORD_BOT_USER_AGENT);
        return headers;
    }
}
