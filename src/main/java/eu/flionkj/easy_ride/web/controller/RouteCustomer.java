package eu.flionkj.easy_ride.web.controller;

import eu.flionkj.easy_ride.domain.CreateRideRequest;
import eu.flionkj.easy_ride.domain.CreateRideResponse;
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

    @PostMapping("/createRide")
    public ResponseEntity<CreateRideResponse> createRide(@RequestBody CreateRideRequest request) {
        CreateRideResponse response = new CreateRideResponse(customerService.createRide(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
