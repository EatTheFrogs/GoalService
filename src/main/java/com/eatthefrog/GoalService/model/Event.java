package com.eatthefrog.GoalService.model;

import lombok.Data;

import java.io.Serial;
import java.time.ZonedDateTime;

@Data
public class Event extends BaseModel {

    @Serial
    private static final long serialVersionUID = 3045590988450013906L;

    private ZonedDateTime createdDate;
    private String name;

}
