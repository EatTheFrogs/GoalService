package com.eatthefrog.GoalService.service;

import com.eatthefrog.GoalService.model.Goal;
import com.eatthefrog.GoalService.repository.GoalRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepo goalRepo;

    public Collection<Goal> getGoals(String userUuid) {
        return goalRepo.findAllByUserUuidOrderByUpdatedDateDesc(userUuid);
    }

    public Collection<Goal> saveGoal(Goal goal) {
        goal.setUpdatedDate(ZonedDateTime.now(ZoneId.of("UTC")));
        goalRepo.save(goal);
        return getGoals(goal.getUserUuid());
    }

    public Collection<Goal> deleteGoal(Goal goal) {
        goalRepo.deleteById(goal.getId());
        return getGoals(goal.getUserUuid());
    }
}
