package eu.flionkj.easy_ride.data.repository;

import eu.flionkj.easy_ride.domain.stopping_points.StoppingPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StoppingPointRepository extends MongoRepository<StoppingPoint, String> {
    Optional<StoppingPoint> findByName(String name);
}
