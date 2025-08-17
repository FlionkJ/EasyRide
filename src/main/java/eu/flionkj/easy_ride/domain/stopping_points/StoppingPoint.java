package eu.flionkj.easy_ride.domain.stopping_points;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record StoppingPoint(@Id String name) {
}
