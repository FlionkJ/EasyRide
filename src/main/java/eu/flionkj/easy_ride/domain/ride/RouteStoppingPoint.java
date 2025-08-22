package eu.flionkj.easy_ride.domain.ride;

import eu.flionkj.easy_ride.domain.stopping_points.StoppingPoint;

import java.util.List;

public record RouteStoppingPoint(
        StoppingPoint stoppingPoint,
        List<String> pickups,
        List<String> dropOffs
) {
}
