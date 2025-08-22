package eu.flionkj.easy_ride.web.service;

import eu.flionkj.easy_ride.data.MongoDB;
import eu.flionkj.easy_ride.domain.route.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    private final MongoDB db;

    public DriverService(MongoDB db) {
        this.db = db;
    }

    public GetRoutesResponseDto getRoutes(String driverId) {
        // validate request
        if (driverId == null || driverId.isBlank()) {
            return new GetRoutesResponseDto(GetRoutesResult.DRIVER_IS_EMPTY, null);
        }

        if (!db.doesDriverExist(driverId)) {
            return new GetRoutesResponseDto(GetRoutesResult.DRIVER_NOT_FOUND, null);
        }

        List<Route> routes = db.getRoutesByDriverId(driverId);

        return new GetRoutesResponseDto(GetRoutesResult.SUCCESS, routes);
    }

    public UpdateRouteResult updateRouteResult(String routeId, RouteStatus newStatus) {
        // validate request
        if (routeId == null || routeId.isBlank()) {
            return UpdateRouteResult.ROUTE_ID_IS_EMPTY;
        }

        if (!db.doesRouteExist(routeId)) {
            return UpdateRouteResult.ROUTE_ID_NOT_FOUND;
        }

        db.updateRouteStatus(routeId, newStatus);
        return UpdateRouteResult.UPDATE_STATUS;
    }
}
