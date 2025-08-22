package eu.flionkj.easy_ride.web.controller;

import eu.flionkj.easy_ride.domain.DefaultResponse;
import eu.flionkj.easy_ride.domain.route.GetRoutesResponse;
import eu.flionkj.easy_ride.domain.route.GetRoutesResponseDto;
import eu.flionkj.easy_ride.domain.route.UpdateRouteResult;
import eu.flionkj.easy_ride.domain.route.UpdateRouteStatusRequest;
import eu.flionkj.easy_ride.web.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
public class RouteDriver {

    private final DriverService driverService;

    @Autowired
    public RouteDriver(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/{driverId}/routes")
    public ResponseEntity<GetRoutesResponse> getRoutes(@PathVariable String driverId) {
        GetRoutesResponseDto dto = driverService.getRoutes(driverId);

        return switch (dto.status()) {
            case DRIVER_NOT_FOUND ->
                    new ResponseEntity<>(new GetRoutesResponse("Driver not found.", null), HttpStatus.NOT_FOUND);
            case DRIVER_IS_EMPTY ->
                    new ResponseEntity<>(new GetRoutesResponse("Driver is empty.", null), HttpStatus.BAD_REQUEST);
            case NO_ROUTES_FOUND ->
                    new ResponseEntity<>(new GetRoutesResponse("No routes found.", null), HttpStatus.NOT_FOUND);
            case SUCCESS -> new ResponseEntity<>(new GetRoutesResponse("Routes found.", dto.routes()), HttpStatus.OK);
        };

    }

    @PatchMapping("/{routeId}/status")
    public ResponseEntity<DefaultResponse> updateRouteStatus(
            @PathVariable String routeId,
            @RequestBody UpdateRouteStatusRequest request) {
        UpdateRouteResult result = driverService.updateRouteResult(routeId, request.status());

        return switch (result) {
            case ROUTE_ID_IS_EMPTY -> new ResponseEntity<>(new DefaultResponse("Route id is empty."), HttpStatus.BAD_REQUEST);
            case ROUTE_ID_NOT_FOUND ->  new ResponseEntity<>(new DefaultResponse("Route id not found."), HttpStatus.NOT_FOUND);
            case UPDATE_STATUS -> new ResponseEntity<>(new DefaultResponse("Route status updated."), HttpStatus.OK);
        };
    }
}
