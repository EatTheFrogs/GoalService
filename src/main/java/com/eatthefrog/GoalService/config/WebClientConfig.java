package com.eatthefrog.GoalService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    public static final String EVENT_SERVICE_WEBCLIENT = "eventServiceWebClient";
    public static final String EVENTTEMPLATE_SERVICE_WEBCLIENT = "eventTemplateServiceWebClient";

    @Value("${EventService.url}")
    private String EVENT_SERVICE_URL;

    @Value("${EventService.context-path}")
    private String EVENT_SERVICE_PATH;

    @Value("${EventTemplateService.url}")
    private String EVENTTEMPLATE_SERVICE_URL;

    @Value("${EventTemplateService.context-path}")
    private String EVENTTEMPLATE_SERVICE_PATH;

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                 OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .refreshToken()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean(name = EVENT_SERVICE_WEBCLIENT)
    public WebClient eventServiceWebClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("okta");

        return WebClient.builder()
                .apply(oauth2Client.oauth2Configuration())
                .baseUrl(EVENT_SERVICE_URL + EVENT_SERVICE_PATH)
                .build();
    }

    @Bean(name = EVENTTEMPLATE_SERVICE_WEBCLIENT)
    public WebClient eventTemplateWebClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("okta");

        return WebClient.builder()
                .apply(oauth2Client.oauth2Configuration())
                .baseUrl(EVENTTEMPLATE_SERVICE_URL + EVENTTEMPLATE_SERVICE_PATH)
                .build();
    }
}
