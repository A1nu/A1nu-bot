package ee.a1nu.application.web;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

public class OAuth2TestData {
    private OAuth2TestData() {}

    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String AUTHORIZATION_URI = "http://example.com/authorization";
    public static final String TOKEN_URI = "http://example.com/token";
    public static final String REDIRECT_URI = "http://example.com/redirect";
    public static final String USER_INFO_URI = "http://example.com/user";
    public static final String USERNAME_ATTRIBUTE_NAME = "username";
    public static final String SCOPE = "identity";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";


    public static ClientRegistration testClient() {
        return ClientRegistration.withRegistrationId("testClient")
                .clientName("Test Client")
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .authorizationUri(AUTHORIZATION_URI)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .tokenUri(TOKEN_URI)
                .userInfoUri(USER_INFO_URI)
                .userNameAttributeName(USERNAME_ATTRIBUTE_NAME)
                .redirectUriTemplate(REDIRECT_URI)
                .scope(SCOPE)
                .build();
    }
}
