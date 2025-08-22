package eu.flionkj.easy_ride.domain.ride;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document
public record Route(
        @Id String id,
        String driverId,
        RouteStatus status,
        List<RouteStoppingPoint> plannedStops
) {
}
