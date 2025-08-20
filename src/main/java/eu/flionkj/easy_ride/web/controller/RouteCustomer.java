package eu.flionkj.easy_ride.web.controller;

import eu.flionkj.easy_ride.domain.customer.AddCustomerRequest;
import eu.flionkj.easy_ride.domain.customer.AddCustomerResult;
import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import eu.flionkj.easy_ride.domain.DefaultResponse;
import eu.flionkj.easy_ride.domain.ride.CreateRideResult;
import eu.flionkj.easy_ride.web.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            case START_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("Start cannot be empty."), HttpStatus.BAD_REQUEST);
            case START_POINT_NOT_FOUND ->
                    new ResponseEntity<>(new DefaultResponse("Start point not found."), HttpStatus.BAD_REQUEST);
            case END_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("End cannot be empty."), HttpStatus.BAD_REQUEST);
            case END_POINT_NOT_FOUND ->
                    new ResponseEntity<>(new DefaultResponse("End point not found."), HttpStatus.BAD_REQUEST);
            case CREATED_SUCCESSFULLY ->
                    new ResponseEntity<>(new DefaultResponse("Ride created successfully."), HttpStatus.CREATED);

        };

    }

    @PostMapping("/add")
    public ResponseEntity<DefaultResponse> addCustomer(@RequestBody AddCustomerRequest request) {
        AddCustomerResult result = customerService.addCustomer(request);

        return switch (result) {
            case NAME_IS_EMPTY ->  new ResponseEntity<>(new DefaultResponse("Name cannot be empty."), HttpStatus.BAD_REQUEST);
            case CREATED_SUCCESSFULLY ->   new ResponseEntity<>(new DefaultResponse("Customer added successfully."), HttpStatus.CREATED);
        };
    }
}
