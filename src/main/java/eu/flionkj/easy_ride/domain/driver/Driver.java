package eu.flionkj.easy_ride.domain.driver;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Driver(
        String name,
        int passenger
) {
}
