package eu.flionkj.easy_ride.domain.ride;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record RideToProcess(
        @Id
        String id,
        String name,
        String start,
        String end
) {
    public RideToProcess(String name, String start, String end) {
        this(null, name, start, end);
    }
}
