package eu.flionkj.easy_ride.web.service;

import eu.flionkj.easy_ride.data.MongoDB;
import eu.flionkj.easy_ride.domain.route.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    private final MongoDB db;

    public DriverService(MongoDB db) {
        this.db = db;
    }

    public GetRoutesDto getRoutes(String driverId) {
        // validate request
        if (driverId == null || driverId.isBlank()) {
            return new GetRoutesDto(GetRoutesResult.DRIVER_IS_EMPTY, null);
        }

        if (!db.doesDriverExist(driverId)) {
            return new GetRoutesDto(GetRoutesResult.DRIVER_NOT_FOUND, null);
        }

        List<Route> routes = db.getRoutesByDriverId(driverId);

        return new GetRoutesDto(GetRoutesResult.SUCCESS, routes);
    }

    public UpdateRouteStatusResult updateRouteResult(String routeId, RouteStatus newStatus) {
        // validate request
        if (routeId == null || routeId.isBlank()) {
            return UpdateRouteStatusResult.ROUTE_ID_IS_EMPTY;
        }

        if (!db.doesRouteExist(routeId)) {
            return UpdateRouteStatusResult.ROUTE_ID_NOT_FOUND;
        }

        db.updateRouteStatus(routeId, newStatus);
        return UpdateRouteStatusResult.UPDATE_STATUS;
    }

    public ReachedRouteStopDto reachedRouteStop(String routeId) {
        // validate request
        if (routeId == null || routeId.isBlank()) {
            return new ReachedRouteStopDto(ReachedRouteStopResult.ROUTE_ID_IS_EMPTY, null);
        }

        if (!db.doesRouteExist(routeId)) {
            return new ReachedRouteStopDto(ReachedRouteStopResult.ROUTE_ID_NOT_FOUND, null);
        }

        if (!db.doesRouteStarted(routeId)) {
            db.updateRouteStatus(routeId, RouteStatus.IN_PROGRESS);
        }

        Optional<RouteStoppingPoint> currentStoppingPoint = db.findFirstStoppingPointOfRoute(routeId);

        return currentStoppingPoint.map(routeStoppingPoint -> new ReachedRouteStopDto(ReachedRouteStopResult.SUCCESS, routeStoppingPoint)).orElseGet(() -> new ReachedRouteStopDto(ReachedRouteStopResult.DB_ERROR, null));
    }
}
