package com.eatthefrog.GoalService.client;

import com.eatthefrog.GoalService.controller.GoalsController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static com.eatthefrog.GoalService.config.WebClientConfig.EVENT_SERVICE_WEBCLIENT;

@Component
@RequiredArgsConstructor
public class EventServiceClient {

    private static final String DELETE_FOR_GOAL_PATH = "/delete/goal/{goalId}";
    private static final String DELETE_FOR_USER_PATH = "/delete/user/{userUuid}";

    @Qualifier(EVENT_SERVICE_WEBCLIENT)
    private final WebClient eventServiceWebClient;

    public void deleteEventsForGoal(String goalId) {
        eventServiceWebClient.delete()
                .uri(uriBuilder -> uriBuilder.path(DELETE_FOR_GOAL_PATH)
                        .build(goalId))
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        response -> response.bodyToMono(String.class).map(GoalsController.OperationFailedException::new))
                .bodyToMono(Object.class)
                .block();
    }

    public void deleteAllEventsForUser(String userUuid) {
        eventServiceWebClient.delete()
                .uri(uriBuilder -> uriBuilder.path(DELETE_FOR_USER_PATH)
                        .build(userUuid))
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        response -> response.bodyToMono(String.class).map(GoalsController.OperationFailedException::new))
                .bodyToMono(Object.class)
                .block();
    }
}
