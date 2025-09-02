package eu.flionkj.easy_ride.domain.ride;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record RideToProcess(
        @Id
        String id,
        String customerId,
        String start,
        String end
) {
    public RideToProcess(String customerId, String start, String end) {
        this(null, customerId, start, end);
    }
}
