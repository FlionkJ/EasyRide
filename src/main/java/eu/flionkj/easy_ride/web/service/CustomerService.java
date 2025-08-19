package eu.flionkj.easy_ride.web.service;

import eu.flionkj.easy_ride.data.MongoDB;
import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import eu.flionkj.easy_ride.domain.ride.CreateRideResult;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointResult;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final MongoDB db;

    public CustomerService(MongoDB db) {
        this.db = db;
    }

    public CreateRideResult createRide(CreateRideRequest request) {
        // validate request
        if (request.id() == null || request.id().isEmpty()) {
            return CreateRideResult.ID_IS_EMPTY;
        }
        if (request.start() == null || request.start().isEmpty()) {
            return CreateRideResult.START_IS_EMPTY;
        }
        if (request.end() == null || request.end().isEmpty()) {
            return CreateRideResult.END_IS_EMPTY;
        }

        // check if the stopping points exists
        if (!db.doesStoppingPointExist(request.start())) {
            return CreateRideResult.START_POINT_NOT_FOUND;
        }
        if (!db.doesStoppingPointExist(request.end())) {
            return CreateRideResult.END_POINT_NOT_FOUND;
        }

        db.addRide(request);
        return CreateRideResult.CREATED_SUCCESSFULLY;
    }

}
