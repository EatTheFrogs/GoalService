package com.eatthefrog.GoalService.model.eventtemplate;

import com.eatthefrog.GoalService.model.BaseModel;
import lombok.Data;

import java.io.Serial;

@Data
public class LiftTemplate extends BaseModel {

    @Serial
    private static final long serialVersionUID = 2915210869392065675L;

    private String name;
}