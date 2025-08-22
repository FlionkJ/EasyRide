package eu.flionkj.easy_ride.domain.driver;

import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Driver(
        @Id String id,
        String name,
        int passenger
) {
}
