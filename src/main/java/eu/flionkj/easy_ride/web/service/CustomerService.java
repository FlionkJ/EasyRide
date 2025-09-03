package eu.flionkj.easy_ride.web.service;

import eu.flionkj.easy_ride.data.MongoDB;
import eu.flionkj.easy_ride.domain.customer.*;
import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import eu.flionkj.easy_ride.domain.ride.CreateRideResult;
import eu.flionkj.easy_ride.domain.ride.RideProcessed;
import eu.flionkj.easy_ride.domain.route.RouteStatus;
import eu.flionkj.easy_ride.service.RoutePlanningService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final MongoDB db;
    private final RoutePlanningService routePlanningService;

    public CustomerService(MongoDB db, RoutePlanningService routePlanningService) {
        this.db = db;
        this.routePlanningService = routePlanningService;
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

        // check if customer exists
        if (!db.doesCustomerExist(request.id())) {
            return CreateRideResult.ID_NOT_FOUND;
        }

        db.addRide(request);
        return CreateRideResult.CREATED_SUCCESSFULLY;
    }

    public RegisterCustomerDto registerCustomer(RegisterCustomerRequest request) {
        // validate request
        if (request.name() == null || request.name().isEmpty()) {
            return new RegisterCustomerDto(RegisterCustomerResult.NAME_IS_EMPTY, null);
        }

        String customerId = db.addCustomer(request);
        return new RegisterCustomerDto(RegisterCustomerResult.CREATED_SUCCESSFULLY, customerId);
    }

    public Pair<WaitTimeResult, Optional<Integer>> getWaitTime(String customerId, WaitTime reqestType) {
        if (customerId == null || customerId.isEmpty()) {
            return Pair.of(WaitTimeResult.ID_IS_EMPTY, Optional.empty());
        }
        if (!db.doesCustomerExist(customerId)) {
            return Pair.of(WaitTimeResult.ID_NOT_FOUND, Optional.empty());
        }
        Optional<RideProcessed> foundCustomerRide = db.findProcessedRideById(customerId);
        RideProcessed curCustomerRide = foundCustomerRide.orElse(null);
        if (curCustomerRide == null) {
            return Pair.of(WaitTimeResult.DB_ERROR, Optional.empty());
        }
        if (db.getRouteStatus(curCustomerRide.RouteId()) == RouteStatus.PLANNED) {
            return Pair.of(WaitTimeResult.ROUTE_NOT_STARTED, Optional.empty());
        }

        return switch (reqestType) {
            case PICKUP -> {
                if (routePlanningService.hasCustomerBeenPickedUp(curCustomerRide)) {
                    yield Pair.of(WaitTimeResult.ALREADY_PICKED_UP, Optional.empty());
                }
                yield Pair.of(WaitTimeResult.SUCCESS, Optional.of(routePlanningService.getRoutePickupWaitTime(curCustomerRide)));
            }
            case DROP_OFF -> {
                if (!routePlanningService.hasCustomerBeenPickedUp(curCustomerRide)) {
                    yield Pair.of(WaitTimeResult.WAITING_FOR_PICKUP, Optional.empty());
                }
                yield Pair.of(WaitTimeResult.SUCCESS, Optional.of(routePlanningService.getRouteDropOffWaitTime(curCustomerRide)));
            }
        };
    }
}
