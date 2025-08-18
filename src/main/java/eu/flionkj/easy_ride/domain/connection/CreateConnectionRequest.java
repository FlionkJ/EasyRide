package eu.flionkj.easy_ride.domain.connection;

public record CreateConnectionRequest(String start, String end, int averageTravelTimeMinutes) {
}
