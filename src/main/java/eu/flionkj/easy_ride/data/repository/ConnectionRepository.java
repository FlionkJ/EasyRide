package eu.flionkj.easy_ride.data.repository;

import eu.flionkj.easy_ride.domain.connection.Connection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRepository extends MongoRepository<Connection, String> {
    boolean existsByStartAndEnd(String start, String end);
}
