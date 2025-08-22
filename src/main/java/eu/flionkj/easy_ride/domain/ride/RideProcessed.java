package eu.flionkj.easy_ride.domain.ride;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record RideProcessed(
        @Id String id,
        String name,
        String start,
        String end,
        String RouteId
) {

    public RideProcessed(RideToProcess ride, String RouteId) {
        this(null, ride.name(), ride.start(), ride.end(), RouteId);
    }
}
