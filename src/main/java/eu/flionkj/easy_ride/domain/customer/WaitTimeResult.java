package eu.flionkj.easy_ride.domain.customer;

public enum WaitTimeResult {
    ID_IS_EMPTY,
    ID_NOT_FOUND,
    NO_RIDE_FOUND,
    DB_ERROR,
    ROUTE_NOT_STARTED,
    WAITING_FOR_PICKUP,
    ALREADY_PICKED_UP,
    SUCCESS,
}
