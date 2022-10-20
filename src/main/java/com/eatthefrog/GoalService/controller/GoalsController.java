package com.eatthefrog.GoalService.controller;

import com.eatthefrog.GoalService.model.Event;
import com.eatthefrog.GoalService.model.Goal;
import com.eatthefrog.GoalService.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class GoalsController {

    @Value("${EAT.THE.FROG.OKTA.BACKEND.SCOPE}")
    private String scope;

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

    @PreAuthorize("hasAuthority('SCOPE_'+#scope) or #userUuid == authentication.token.claims['uid']")
    @GetMapping("/{userUuid}")
    public Collection<Goal> getGoalsForUser(@PathVariable String userUuid) {
        return goalService.getGoalsForUser(userUuid);
    }

    @PreAuthorize("hasAuthority('SCOPE_'+#scope)")
    @PostMapping("/create/event")
    public ResponseEntity addEventToGoal(@RequestBody Event event) {
        goalService.addEventToGoal(event);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_'+#scope)")
    @PostMapping("/delete/event")
    public ResponseEntity updateEventForGoal(@RequestBody Event event) {
        goalService.deleteEventFromGoal(event);
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

    @PreAuthorize("#goal.getUserUuid() == authentication.token.claims['uid']")
    @DeleteMapping("/delete")
    public Collection<Goal> deleteGoal(@RequestBody Goal goal) {
        return goalService.deleteGoal(goal);
    }
}
