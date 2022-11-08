package com.eatthefrog.GoalService.model.goal;

import com.eatthefrog.GoalService.model.BaseModel;
import com.eatthefrog.GoalService.model.event.Event;
import com.eatthefrog.GoalService.model.eventtemplate.EventTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "goals")
public class Goal extends BaseModel implements Comparable<Goal> {

    @Serial
    private static final long serialVersionUID = -8357126405162601677L;

    @Id
    private String id;
    private ZonedDateTime createdDate;
    @NotNull
    private String userUuid;
    private String name;
    private String description;
    @DBRef
    private List<Event> completedEvents = new ArrayList<Event>();
    @DBRef
    private List<EventTemplate> eventTemplates = new ArrayList<EventTemplate>();

    @Override
    public int compareTo(Goal goal) {

        var thisEvents = this.completedEvents;
        var thatEvents = goal.getCompletedEvents();

        if(thisEvents.isEmpty()) {
            return 1;
        } else if(thatEvents.isEmpty()) {
            return -1;
        }

        return thatEvents.get(0).getCompletedDate().compareTo(thisEvents.get(0).getCompletedDate());
    }
}
