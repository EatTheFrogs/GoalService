package com.eatthefrog.GoalService.service;

import com.eatthefrog.GoalService.client.EventServiceClient;
import com.eatthefrog.GoalService.controller.GoalsController;
import com.eatthefrog.GoalService.model.Event;
import com.eatthefrog.GoalService.model.Goal;
import com.eatthefrog.GoalService.repository.GoalRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Log
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepo goalRepo;
    private final EventServiceClient eventServiceClient;

    public Collection<Goal> getGoalById(String goalId) {
        return goalRepo.findAllById(goalId);
    }
    public Collection<Goal> getGoalsForUser(String userUuid) {
        return goalRepo.findAllByUserUuidOrderByCreatedDateDesc(userUuid);
    }

    public Collection<Goal> createGoal(Goal goal) {
        goal.setCreatedDate(ZonedDateTime.now(ZoneId.of("UTC")));
        goalRepo.save(goal);
        return getGoalsForUser(goal.getUserUuid());
    }

    public Collection<Goal> updateGoal(Goal goal) {
        goalRepo.save(goal);
        return getGoalsForUser(goal.getUserUuid());
    }

    public Collection<Goal> deleteGoal(Goal goal) {
        deleteGoalTransactional(goal);
        return getGoalsForUser(goal.getUserUuid());
    }

    public Collection<Goal> addEventToGoal(Event event) {
        Goal goal = getGoalById(event.getGoalId())
                .stream().findFirst()
                .orElseThrow(() -> new GoalsController.ResourceNotFoundException("Couldn't find goal with id "+event.getGoalId()));
        goal.getCompletedEvents().add(event);
        return updateGoal(goal);
    }

    public Collection<Goal> deleteEventFromGoal(String eventId, String goalId) {
        Goal goal = getGoalById(goalId)
                .stream().findFirst()
                .orElseThrow(() -> new GoalsController.ResourceNotFoundException("Couldn't find goal with id "+goalId));
        List<Event> events = goal.getCompletedEvents().stream().filter(currEvent -> !currEvent.getId().equals(eventId)).toList();
        goal.setCompletedEvents(events);
        return updateGoal(goal);
    }

    @Transactional(rollbackFor=Exception.class)
    private void deleteGoalTransactional(Goal goal) {
        try {
            goalRepo.deleteById(goal.getId());
            eventServiceClient.deleteEventsForGoal(goal.getId());
        } catch(Exception e) {
            log.severe(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }
}
