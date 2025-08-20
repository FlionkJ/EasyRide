package eu.flionkj.easy_ride.domain.customer;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Customer(
        String name
) {
}
