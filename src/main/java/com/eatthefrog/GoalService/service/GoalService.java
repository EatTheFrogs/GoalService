package com.eatthefrog.GoalService.service;

import com.eatthefrog.GoalService.client.EventServiceClient;
import com.eatthefrog.GoalService.client.EventTemplateServiceClient;
import com.eatthefrog.GoalService.controller.GoalsController;
import com.eatthefrog.GoalService.model.event.Event;
import com.eatthefrog.GoalService.model.eventtemplate.EventTemplate;
import com.eatthefrog.GoalService.model.goal.Goal;
import com.eatthefrog.GoalService.repository.GoalRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepo goalRepo;
    private final EventServiceClient eventServiceClient;
    private final EventTemplateServiceClient eventTemplateServiceClient;
    private final TransactionHandlerService transactionHandlerService;

    public Goal getGoalById(String goalId) {
        return goalRepo.findById(goalId).orElseThrow(() -> new GoalsController.ResourceNotFoundException("Couldn't find goal with id "+goalId));
    }
    public Collection<Goal> getGoalsForUser(String userUuid) {
        List<Goal> goals = goalRepo.findAllByUserUuidOrderByCreatedDateDesc(userUuid).stream().collect(Collectors.toList());
        goals.forEach(goal -> Collections.sort(goal.getCompletedEvents()));
        Collections.sort(goals);
        return goals;
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

    // Used in @Preauthorize annotation on controller
    public boolean assertUserOwnsGoal(String userUuid, String goalId) {
        Goal goal = getGoalById(goalId);
        return StringUtils.equals(userUuid, goal.getUserUuid());
    }

    public Collection<Goal> deleteGoal(String goalId, String userUuid) throws Exception {
        transactionHandlerService.runInTransaction(() -> deleteGoalTransactional(goalId));
        return getGoalsForUser(userUuid);
    }

    public void deleteAllGoalsForUser(String userUuid) throws Exception {
        transactionHandlerService.runInTransaction(() -> deleteAllGoalsForUserTransactional(userUuid));
    }

    public void addEventToGoal(Event event) {
        Goal goal = getGoalById(event.getGoalId());
        List<Event> events = goal.getCompletedEvents();
        events.add(event);
        Collections.sort(events);
        goal.setCompletedEvents(events);
        goalRepo.save(goal);
    }

    public void addEventTemplateToGoal(EventTemplate eventTemplate) {
        Goal goal = getGoalById(eventTemplate.getGoalId());
        goal.getEventTemplates().add(eventTemplate);
        goalRepo.save(goal);
    }

    public void deleteEventFromGoal(String eventId, String goalId) {
        Goal goal = getGoalById(goalId);
        List<Event> events = goal.getCompletedEvents().stream().filter(currEvent -> !currEvent.getId().equals(eventId)).toList();
        goal.setCompletedEvents(events);
        goalRepo.save(goal);
    }

    public void deleteTemplateFromGoal(String templateId, String goalId) {
        Goal goal = getGoalById(goalId);
        List<EventTemplate> templates = goal.getEventTemplates().stream().filter(currTemplate -> !currTemplate.getId().equals(templateId)).toList();
        goal.setEventTemplates(templates);
        goalRepo.save(goal);
    }

    public void deleteGoalTransactional(String goalId) {
        goalRepo.deleteById(goalId);
        eventServiceClient.deleteEventsForGoal(goalId);
        eventTemplateServiceClient.deleteTemplatesForGoal(goalId);
    }

    public void deleteAllGoalsForUserTransactional(String userUuid) {
        goalRepo.deleteByUserUuid(userUuid);
        eventServiceClient.deleteAllEventsForUser(userUuid);
        eventTemplateServiceClient.deleteAllTemplatesForUser(userUuid);
    }
}
