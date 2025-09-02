package eu.flionkj.easy_ride.domain.customer;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Customer(
        @Id String id,
        String name
) {
    public Customer(String name) {
        this(null, name);
    }
}
