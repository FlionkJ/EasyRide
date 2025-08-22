package eu.flionkj.easy_ride.data.repository;

import eu.flionkj.easy_ride.domain.route.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {
    List<Route> findByDriverId(String driverId);
}
