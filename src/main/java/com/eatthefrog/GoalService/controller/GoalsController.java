package com.eatthefrog.GoalService.controller;

import com.eatthefrog.GoalService.model.Goal;
import com.eatthefrog.GoalService.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class GoalsController {

    private final GoalService goalService;

    @PreAuthorize("#userUuid == authentication.token.claims['uid']")
    @GetMapping("/{userUuid}")
    public Collection<Goal> getGoalsForUser(@PathVariable String userUuid) {
        return goalService.getGoals(userUuid);
    }

    @PreAuthorize("#goal.getUserUuid() == authentication.token.claims['uid']")
    @PostMapping("/create")
    public Collection<Goal> createGoal(@RequestBody Goal goal) {
        return goalService.saveGoal(goal);
    }

    @PreAuthorize("#goal.getUserUuid() == authentication.token.claims['uid']")
    @PatchMapping("/update")
    public Collection<Goal> updateGoal(@RequestBody Goal goal) {
        return goalService.saveGoal(goal);
    }

    @PreAuthorize("#goal.getUserUuid() == authentication.token.claims['uid']")
    @DeleteMapping("/delete")
    public Collection<Goal> deleteGoal(@RequestBody Goal goal) {
        return goalService.deleteGoal(goal);
    }
}
