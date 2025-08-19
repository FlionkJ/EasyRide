package eu.flionkj.easy_ride.web.controller;

import eu.flionkj.easy_ride.domain.DefaultResponse;
import eu.flionkj.easy_ride.domain.connection.CreateConnectionRequest;
import eu.flionkj.easy_ride.domain.connection.CreateConnectionResult;
import eu.flionkj.easy_ride.domain.driver.AddDriverRequest;
import eu.flionkj.easy_ride.domain.driver.AddDriverResult;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointRequest;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointResult;
import eu.flionkj.easy_ride.web.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class RouteAdmin {

    private final AdminService adminService;

    @Autowired
    public RouteAdmin(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create/stoppingpoint")
    public ResponseEntity<DefaultResponse> createStoppingPoint(@RequestBody CreateStoppingPointRequest request) {
        CreateStoppingPointResult result = adminService.createStoppingPoint(request);

        return switch (result) {
            case ALREADY_EXISTS ->
                    new ResponseEntity<>(new DefaultResponse("A stopping point with this name already exists."), HttpStatus.CONFLICT);
            case NAME_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("Name cannot be empty."), HttpStatus.BAD_REQUEST);
            case CREATED_SUCCESSFULLY ->
                    new ResponseEntity<>(new DefaultResponse("Stopping point was created successfully."), HttpStatus.CREATED);
        };
    }

    @PostMapping("/create/connection")
    public ResponseEntity<DefaultResponse> createConnection(@RequestBody CreateConnectionRequest request) {
        CreateConnectionResult result = adminService.createConnection(request);

        return switch (result) {
            case ALREADY_EXISTS ->
                    new ResponseEntity<>(new DefaultResponse("A connection with this parameters already exist."), HttpStatus.CONFLICT);
            case START_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("Start cannot be empty."), HttpStatus.BAD_REQUEST);
            case START_POINT_NOT_FOUND ->
                    new ResponseEntity<>(new DefaultResponse("Start Point does not exist."), HttpStatus.NOT_FOUND);
            case END_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("End cannot be empty."), HttpStatus.BAD_REQUEST);
            case END_POINT_NOT_FOUND ->
                    new ResponseEntity<>(new DefaultResponse("End Point does not exist."), HttpStatus.NOT_FOUND);
            case AVERAGE_TRAVEL_TIME_MINUTES_IS_0 ->
                    new ResponseEntity<>(new DefaultResponse("Average travel time must be higher than 0."), HttpStatus.BAD_REQUEST);
            case CREATED_SUCCESSFULLY ->
                    new ResponseEntity<>(new DefaultResponse("Connection was created successfully."), HttpStatus.CREATED);
        };
    }

    @PostMapping("add/driver")
    public ResponseEntity<DefaultResponse> addDriver(@RequestBody AddDriverRequest request) {
        AddDriverResult result = adminService.addDriver(request);

        return switch (result) {
            case NAME_IS_EMPTY ->
                    new ResponseEntity<>(new DefaultResponse("Name cannot be empty."), HttpStatus.BAD_REQUEST);
            case PASSENGER_IS_0 ->
                    new ResponseEntity<>(new DefaultResponse("Passenger must be higher than 0."), HttpStatus.BAD_REQUEST);
            case CREATED_SUCCESSFULLY ->
                    new ResponseEntity<>(new DefaultResponse("Driver added successfully."), HttpStatus.CREATED);
        };
    }
}
