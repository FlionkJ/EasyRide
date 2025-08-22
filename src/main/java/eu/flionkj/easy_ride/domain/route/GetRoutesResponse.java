package eu.flionkj.easy_ride.domain.route;

import java.util.List;

public record GetRoutesResponse(String message,
                                List<Route> routes) {
}
