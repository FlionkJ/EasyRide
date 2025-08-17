package eu.flionkj.easy_ride.domain.ride;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ride_to_process")
public record RideToProcess(
        @Id
        String id,
        String name,
        String startingPoint,
        String destinationPoint
) {
    public RideToProcess(String name, String startingPoint, String destinationPoint) {
        this(null, name, startingPoint, destinationPoint);
    }
}
