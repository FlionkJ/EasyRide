package eu.flionkj.easy_ride.web.controller;

import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import eu.flionkj.easy_ride.domain.DefaultResponse;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointRequest;
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

    @PostMapping("/create/stoppingPoint")
    public ResponseEntity<DefaultResponse> createStoppingPoint(@RequestBody CreateStoppingPointRequest request) {
        DefaultResponse response = new DefaultResponse(adminService.createStoppingPoint(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
