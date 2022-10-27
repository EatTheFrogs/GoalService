package com.eatthefrog.GoalService.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("event.default")
@Document(collection = "events")
public class DefaultEvent extends Event {

    @Serial
    private static final long serialVersionUID = -1908016573732803067L;

    private List<DefaultEventField> fields = new ArrayList<DefaultEventField>();
}