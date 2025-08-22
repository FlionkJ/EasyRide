package eu.flionkj.easy_ride.domain.connection;

public enum CreateConnectionResult {
    CREATED_SUCCESSFULLY,
    ALREADY_EXISTS,
    START_IS_EMPTY,
    START_POINT_NOT_FOUND,
    END_IS_EMPTY,
    END_POINT_NOT_FOUND,
    AVERAGE_TRAVEL_TIME_MINUTES_IS_0,
}
