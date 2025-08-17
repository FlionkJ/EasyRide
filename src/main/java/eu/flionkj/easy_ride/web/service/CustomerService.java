package eu.flionkj.easy_ride.web.service;

import eu.flionkj.easy_ride.data.MongoDB;
import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final MongoDB db;

    public CustomerService(MongoDB db) {
        this.db = db;
    }

    public String createRide(CreateRideRequest request) {
        // Validate request
        if (request.name() == null || request.name().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (request.startingPoint() == null || request.startingPoint().isEmpty()) {
            throw new IllegalArgumentException("Starting Point cannot be empty.");
        }
        if (request.destinationPoint() == null || request.destinationPoint().isEmpty()) {
            throw new IllegalArgumentException("Destination Point cannot be empty.");
        }

        db.addRide(request);

        return "Trip was created successfully.";
    }

}
