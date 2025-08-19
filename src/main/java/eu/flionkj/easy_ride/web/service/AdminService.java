package eu.flionkj.easy_ride.web.service;

import eu.flionkj.easy_ride.data.MongoDB;
import eu.flionkj.easy_ride.domain.connection.CreateConnectionResult;
import eu.flionkj.easy_ride.domain.connection.CreateConnectionRequest;
import eu.flionkj.easy_ride.domain.driver.AddDriverRequest;
import eu.flionkj.easy_ride.domain.driver.AddDriverResult;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointRequest;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointResult;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final MongoDB db;

    public AdminService(MongoDB db) {
        this.db = db;
    }

    public CreateStoppingPointResult createStoppingPoint(CreateStoppingPointRequest request) {
        // Validate request
        if (request.name() == null || request.name().isBlank()) {
            return CreateStoppingPointResult.NAME_IS_EMPTY;
        }

        // check if the stopping point already exists in the database
        if (db.doesStoppingPointExist(request.name())) {
            return CreateStoppingPointResult.ALREADY_EXISTS;
        }

        db.addStop(request);
        return CreateStoppingPointResult.CREATED_SUCCESSFULLY;

    }

    public CreateConnectionResult createConnection(CreateConnectionRequest request) {
        // Validate request
        if (request.start() == null || request.start().isBlank()) {
            return CreateConnectionResult.START_IS_EMPTY;
        }
        if (request.end() == null || request.end().isBlank()) {
            return CreateConnectionResult.END_IS_EMPTY;
        }
        if (request.averageTravelTimeMinutes() <= 1) {
            return CreateConnectionResult.AVERAGE_TRAVEL_TIME_MINUTES_IS_0;
        }

        // check if stop point exist
        if (!db.doesStoppingPointExist(request.start())) {
            return CreateConnectionResult.START_POINT_NOT_FOUND;
        }
        if (!db.doesStoppingPointExist(request.end())) {
            return CreateConnectionResult.END_POINT_NOT_FOUND;
        }

        // check if the connection already exists in the database
        if (db.doesConnectionExist(request.start(), request.end()) || db.doesConnectionExist(request.end(), request.start())) {
            return CreateConnectionResult.ALREADY_EXISTS;
        }

        db.addConnection(request);
        return CreateConnectionResult.CREATED_SUCCESSFULLY;
    }

    public AddDriverResult addDriver(AddDriverRequest request) {
        // Validate request
        if (request.name() == null || request.name().isBlank()) {
            return AddDriverResult.NAME_IS_EMPTY;
        }
        if (request.passenger() <= 0) {
            return AddDriverResult.PASSENGER_IS_0;
        }

        db.addDriver(request);
        return AddDriverResult.CREATED_SUCCESSFULLY;
    }
}
