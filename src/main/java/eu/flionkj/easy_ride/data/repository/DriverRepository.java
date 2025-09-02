package eu.flionkj.easy_ride.data.repository;

import eu.flionkj.easy_ride.domain.driver.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
}
