package eu.flionkj.easy_ride.data.repository;

import eu.flionkj.easy_ride.domain.ride.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {
}
