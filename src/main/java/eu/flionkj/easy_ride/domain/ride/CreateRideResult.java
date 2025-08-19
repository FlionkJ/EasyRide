package eu.flionkj.easy_ride.domain.ride;

public enum CreateRideResult {
    CREATED_SUCCESSFULLY,
    ID_IS_EMPTY,
    START_IS_EMPTY,
    START_POINT_NOT_FOUND,
    END_IS_EMPTY,
    END_POINT_NOT_FOUND,
}
