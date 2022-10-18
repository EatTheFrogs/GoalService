package com.eatthefrog.GoalService.repository;

import com.eatthefrog.GoalService.model.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface GoalRepo extends MongoRepository<Goal, String> {

    public Collection<Goal> findAllByUserUuid(String userUuid);

    public Collection<Goal> findAllByUserUuidOrderByUpdatedDateDesc(String userUuid);
}
