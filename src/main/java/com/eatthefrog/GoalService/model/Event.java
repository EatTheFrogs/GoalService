package com.eatthefrog.GoalService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.time.ZonedDateTime;

@Data
@Document(collection = "events")
public class Event extends BaseModel {

    @Serial
    private static final long serialVersionUID = -3721461690283380472L;

    @Id
    private String id;
    private ZonedDateTime completedDate;
    @NotNull
    private String userUuid;
    private String name;
    @NotNull
    private String goalId;
}
