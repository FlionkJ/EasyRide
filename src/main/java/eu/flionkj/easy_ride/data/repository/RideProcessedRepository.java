package eu.flionkj.easy_ride.data.repository;

import eu.flionkj.easy_ride.domain.ride.RideProcessed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RideProcessedRepository extends MongoRepository<RideProcessed, String> {
    Optional<RideProcessed> findByCustomerId(String customerId
    );
}
