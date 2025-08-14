package eu.flionkj.easy_ride.web.service;

import eu.flionkj.easy_ride.data.DB;
import eu.flionkj.easy_ride.domain.CreateRideRequest;
import eu.flionkj.easy_ride.routing.AbstractRide;
import eu.flionkj.easy_ride.routing.TaxiRide;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final DB db;

    public CustomerService(DB db) {
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

        AbstractRide newRide = new TaxiRide(request.name(), request.startingPoint(), request.destinationPoint());

        db.addRide(newRide);

        return "Trip was created successfully.";
    }

}
