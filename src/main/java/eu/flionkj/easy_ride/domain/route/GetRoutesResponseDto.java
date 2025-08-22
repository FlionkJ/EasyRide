package eu.flionkj.easy_ride.domain.route;

import java.util.List;

public record GetRoutesResponseDto(
        GetRoutesResult status,
        List<Route> routes
) {
}
