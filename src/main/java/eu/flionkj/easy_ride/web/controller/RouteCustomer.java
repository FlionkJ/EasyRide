package eu.flionkj.easy_ride.web.controller;

import eu.flionkj.easy_ride.domain.customer.*;
import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import eu.flionkj.easy_ride.domain.DefaultResponse;
import eu.flionkj.easy_ride.domain.ride.CreateRideResult;
import eu.flionkj.easy_ride.web.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class RouteCustomer {

    private final CustomerService customerService;

    @Autowired
    public RouteCustomer(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/create/ride")
    public ResponseEntity<DefaultResponse> createRide(@RequestBody CreateRideRequest request) {
        CreateRideResult result = customerService.createRide(request);

        return switch (result) {
            case ID_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("ID cannot be empty."), HttpStatus.BAD_REQUEST);
            case ID_NOT_FOUND -> new ResponseEntity<>(new DefaultResponse("ID not found."), HttpStatus.NOT_FOUND);
            case START_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("Start cannot be empty."), HttpStatus.BAD_REQUEST);
            case START_POINT_NOT_FOUND ->
                    new ResponseEntity<>(new DefaultResponse("Start point not found."), HttpStatus.NOT_FOUND);
            case END_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("End cannot be empty."), HttpStatus.BAD_REQUEST);
            case END_POINT_NOT_FOUND ->
                    new ResponseEntity<>(new DefaultResponse("End point not found."), HttpStatus.NOT_FOUND);
            case CREATED_SUCCESSFULLY ->
                    new ResponseEntity<>(new DefaultResponse("Ride created successfully."), HttpStatus.CREATED);

        };

    }

    @PostMapping("/register")
    public ResponseEntity<RegisterCustomerResponse> registerCustomer(@RequestBody RegisterCustomerRequest request) {
        RegisterCustomerDto result = customerService.registerCustomer(request);

        return switch (result.status()) {
            case NAME_IS_EMPTY ->
                    new ResponseEntity<>(new RegisterCustomerResponse(result.customerId(), "Name cannot be empty."), HttpStatus.BAD_REQUEST);
            case CREATED_SUCCESSFULLY ->
                    new ResponseEntity<>(new RegisterCustomerResponse(result.customerId(), "Customer registered successfully."), HttpStatus.CREATED);
        };
    }

    @GetMapping("/pickupWaitTime")
    public ResponseEntity<DefaultResponse> pickupWaitTime(@RequestParam String customerId) {
        Pair<WaitTimeResult, Optional<Integer>> waitTime = customerService.getWaitTime(customerId, WaitTime.PICKUP);
        return switch (waitTime.getFirst()) {
            //noinspection DuplicatedCode
            case ID_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("ID cannot be empty."), HttpStatus.BAD_REQUEST);
            case ID_NOT_FOUND -> new ResponseEntity<>(new DefaultResponse("ID not found."), HttpStatus.NOT_FOUND);
            case NO_RIDE_FOUND ->  new ResponseEntity<>(new DefaultResponse("No ride found."), HttpStatus.NOT_FOUND);
            case DB_ERROR -> new ResponseEntity<>(new DefaultResponse("Database error."), HttpStatus.INTERNAL_SERVER_ERROR);
            case ALREADY_PICKED_UP ->   new ResponseEntity<>(new DefaultResponse("You are already in the vehicle."), HttpStatus.BAD_REQUEST);
            case WAITING_FOR_PICKUP -> null; // is not thrown by the service
            case ROUTE_NOT_STARTED -> new ResponseEntity<>(new DefaultResponse("Route not started."), HttpStatus.BAD_REQUEST);
            case SUCCESS ->
                //noinspection OptionalGetWithoutIsPresent
                    new ResponseEntity<>(new DefaultResponse("Pickup wait time is: " + waitTime.getSecond().get()), HttpStatus.OK);
        };
    }

    @GetMapping("/dropOffWaitTime")
    public ResponseEntity<DefaultResponse> dropOffWaitTime(@RequestParam String customerId) {
        Pair<WaitTimeResult, Optional<Integer>> waitTime = customerService.getWaitTime(customerId, WaitTime.DROP_OFF);
        return switch (waitTime.getFirst()) {
            //noinspection DuplicatedCode
            case ID_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("ID cannot be empty."), HttpStatus.BAD_REQUEST);
            case ID_NOT_FOUND -> new ResponseEntity<>(new DefaultResponse("ID not found."), HttpStatus.NOT_FOUND);
            case NO_RIDE_FOUND ->  new ResponseEntity<>(new DefaultResponse("No ride found."), HttpStatus.NOT_FOUND);
            case DB_ERROR -> new ResponseEntity<>(new DefaultResponse("Database error."), HttpStatus.INTERNAL_SERVER_ERROR);
            case ALREADY_PICKED_UP -> null; //is not thrown by the service
            case WAITING_FOR_PICKUP -> new ResponseEntity<>(new DefaultResponse("You are waiting for the vehicle."), HttpStatus.BAD_REQUEST);
            case ROUTE_NOT_STARTED -> new ResponseEntity<>(new DefaultResponse("Route not started."), HttpStatus.BAD_REQUEST);
            case SUCCESS ->
                //noinspection OptionalGetWithoutIsPresent
                    new ResponseEntity<>(new DefaultResponse("Drop Off wait time is: " + waitTime.getSecond().get()), HttpStatus.OK);
        };
    }
}
