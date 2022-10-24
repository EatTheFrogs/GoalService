package com.eatthefrog.GoalService.controller;

import com.eatthefrog.GoalService.model.Event;
import com.eatthefrog.GoalService.model.Goal;
import com.eatthefrog.GoalService.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalsController {

    private final GoalService goalService;

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public static class OperationFailedException extends RuntimeException {
        public OperationFailedException(String message) {
            super(message);
        }
    }

    // Internal endpoints

    @PreAuthorize("hasAuthority('SCOPE_api') or #userUuid == authentication.token.claims['uid']")
    @GetMapping("/{userUuid}")
    public Collection<Goal> getGoalsForUser(@PathVariable String userUuid) {
        return goalService.getGoalsForUser(userUuid);
    }

    @PreAuthorize("hasAuthority('SCOPE_api')")
    @PostMapping("/create/event")
    public ResponseEntity addEventToGoal(@RequestBody Event event) {
        goalService.addEventToGoal(event);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_api')")
    @PostMapping("/{goalId}/delete/event/{eventId}")
    public ResponseEntity updateEventForGoal(@PathVariable String goalId, @PathVariable String eventId) {
        goalService.deleteEventFromGoal(eventId, goalId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_api')")
    @DeleteMapping("/delete/user/{userUuid}")
    public ResponseEntity deleteAllGoalsForUser(@PathVariable String userUuid) throws Exception {
        goalService.deleteAllGoalsForUser(userUuid);
        return ResponseEntity.ok().build();
    }


    // External endpoints

    @PreAuthorize("#goal.getUserUuid() == authentication.token.claims['uid']")
    @PostMapping("/create")
    public Collection<Goal> createGoal(@RequestBody Goal goal) {
        return goalService.createGoal(goal);
    }

    @PreAuthorize("#goal.getUserUuid() == authentication.token.claims['uid']")
    @PatchMapping("/update")
    public Collection<Goal> updateGoal(@RequestBody Goal goal) {
        return goalService.updateGoal(goal);
    }

    @PreAuthorize("@goalService.assertUserOwnsGoal(#jwt.getClaim('uid').toString(), #goalId)")
    @DeleteMapping("/delete/{goalId}")
    public Collection<Goal> deleteGoal(@AuthenticationPrincipal Jwt jwt, @PathVariable String goalId) throws Exception {
        return goalService.deleteGoal(goalId, jwt.getClaim("uid").toString());
    }
}
