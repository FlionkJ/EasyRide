package eu.flionkj.easy_ride.domain.stopping_points;

public record Connection (StoppingPoint start, StoppingPoint end, int averageTravelTimeMinutes){
}
