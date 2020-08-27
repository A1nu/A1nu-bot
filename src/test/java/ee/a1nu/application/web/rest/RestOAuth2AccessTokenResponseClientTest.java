package ee.a1nu.application.web.rest;

import ee.a1nu.application.web.OAuth2TestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.web.client.RestOperations;

import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.http.HttpMethod.POST;

@RunWith(MockitoJUnitRunner.class)
public class RestOAuth2AccessTokenResponseClientTest {
    @Mock
    private RestOperations restOperations;

    @InjectMocks
    private RestOAuth2AccessTokenResponseClient client;

    @Captor
    private ArgumentCaptor<HttpEntity<Map<String, Object>>> requestCaptor;

    @Test
    public void shouldExchangeAccessToken() {
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(OAuth2TestData.CLIENT_ID)
                .redirectUri(OAuth2TestData.REDIRECT_URI)
                .authorizationUri(OAuth2TestData.AUTHORIZATION_URI)
                .scope(OAuth2TestData.SCOPE)
                .build();

        OAuth2AuthorizationResponse authResponse = OAuth2AuthorizationResponse.success(OAuth2TestData.ACCESS_TOKEN)
                .redirectUri(OAuth2TestData.REDIRECT_URI)
                .build();

        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authRequest, authResponse);

        when(restOperations.exchange(eq(OAuth2TestData.TOKEN_URI), eq(POST), requestCaptor.capture(), eq(RestOAuth2AccessTokenResponseClient.AccessResponse.class)))
                .thenReturn(ResponseEntity.ok(new RestOAuth2AccessTokenResponseClient.AccessResponse(OAuth2TestData.ACCESS_TOKEN, "Bearer", 10, OAuth2TestData.REFRESH_TOKEN, OAuth2TestData.SCOPE)));

        client.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(OAuth2TestData.testClient(), authorizationExchange));

        HttpEntity<Map<String, Object>> request = requestCaptor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.getHeaders()).containsKey(USER_AGENT);
        assertThat(request.getHeaders()).containsEntry(CONTENT_TYPE, singletonList(MediaType.APPLICATION_FORM_URLENCODED_VALUE));

        Map<String, Object> requestBody = request.getBody();
        assertThat(requestBody).isNotNull();
        assertThat(requestBody).containsEntry("client_id", singletonList(OAuth2TestData.CLIENT_ID));
        assertThat(requestBody).containsEntry("client_secret", singletonList(OAuth2TestData.CLIENT_SECRET));
        assertThat(requestBody).containsEntry("grant_type", singletonList(AuthorizationGrantType.AUTHORIZATION_CODE.getValue()));
        assertThat(requestBody).containsEntry("code", singletonList(OAuth2TestData.ACCESS_TOKEN));
        assertThat(requestBody).containsEntry("redirect_uri", singletonList(OAuth2TestData.REDIRECT_URI));
        assertThat(requestBody).containsEntry("scope", singletonList(OAuth2TestData.SCOPE));
    }
}

