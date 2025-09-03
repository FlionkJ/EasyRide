package eu.flionkj.easy_ride.service;

import eu.flionkj.easy_ride.data.repository.*;
import eu.flionkj.easy_ride.domain.connection.Connection;
import eu.flionkj.easy_ride.domain.driver.Driver;
import eu.flionkj.easy_ride.domain.ride.RideProcessed;
import eu.flionkj.easy_ride.domain.ride.RideToProcess;
import eu.flionkj.easy_ride.domain.route.Route;
import eu.flionkj.easy_ride.domain.route.RouteStatus;
import eu.flionkj.easy_ride.domain.route.RouteStoppingPoint;
import eu.flionkj.easy_ride.domain.route.RouteStoppingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoutePlanningService {

    private static final Logger logger = LoggerFactory.getLogger(RoutePlanningService.class);

    private final RideToProcessRepository rideToProcessRepository;
    private final ConnectionRepository connectionRepository;
    private final RouteRepository routeRepository;
    private final RideProcessedRepository rideProcessedRepository;
    private final DriverRepository driverRepository;
    private final StoppingPointRepository stoppingPointRepository;

    @Autowired
    public RoutePlanningService(RideToProcessRepository rideToProcessRepository,
                                ConnectionRepository connectionRepository, RouteRepository routeRepository,
                                RideProcessedRepository rideProcessedRepository, DriverRepository driverRepository,
                                StoppingPointRepository stoppingPointRepository) {
        this.rideToProcessRepository = rideToProcessRepository;
        this.connectionRepository = connectionRepository;
        this.routeRepository = routeRepository;
        this.rideProcessedRepository = rideProcessedRepository;
        this.driverRepository = driverRepository;
        this.stoppingPointRepository = stoppingPointRepository;
    }

    public List<Route> planRoutes() {
        logger.info("Starting a route planning session to distribute rides evenly among all available drivers.");
        List<Route> newRoutes = new ArrayList<>();
        List<RideToProcess> rides = rideToProcessRepository.findAll();
        List<Connection> connections = connectionRepository.findAll();
        List<Driver> drivers = driverRepository.findAll();

        if (drivers.isEmpty()) {
            logger.warn("No drivers available to plan routes. Skipping route planning.");
            return newRoutes;
        }

        if (rides.isEmpty()) {
            logger.info("No new rides to process. Skipping route planning.");
            return newRoutes;
        }

        // create the graph
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        for (Connection connection : connections) {
            graph.computeIfAbsent(connection.start(), k -> new HashMap<>()).put(connection.end(), connection.averageTravelTimeMinutes());
            graph.computeIfAbsent(connection.end(), k -> new HashMap<>()).put(connection.start(), connection.averageTravelTimeMinutes());
        }

        // distribute rides among drivers in a round-robin fashion
        Map<Driver, List<RideToProcess>> driverRides = new HashMap<>();
        for (Driver value : drivers) {
            driverRides.put(value, new ArrayList<>());
        }
        for (int i = 0; i < rides.size(); i++) {
            Driver driver = drivers.get(i % drivers.size());
            driverRides.get(driver).add(rides.get(i));
        }

        // plan a route for each driver
        for (Map.Entry<Driver, List<RideToProcess>> entry : driverRides.entrySet()) {
            Driver driver = entry.getKey();
            List<RideToProcess> assignedRides = entry.getValue();

            if (assignedRides.isEmpty()) {
                logger.info("Driver {} has no rides assigned. Skipping.", driver.id());
                continue;
            }

            Route plannedRoute = planSingleRouteForDriver(driver, assignedRides, graph);
            if (plannedRoute != null) {
                newRoutes.add(plannedRoute);
                logger.info("Route for driver {} created successfully. Route ID: {}", driver.id(), plannedRoute.id());
            }
        }

        logger.info("Route planning completed. Created {} new routes.", newRoutes.size());
        return newRoutes;
    }

    @SuppressWarnings("unchecked")
    private Route planSingleRouteForDriver(Driver driver, List<RideToProcess> rides, Map<String, Map<String, Integer>> graph) {
        String currentStop = "Central Hub";
        List<RouteStoppingPoint> RouteStops = new ArrayList<>();
        List<RideToProcess> ridesToPickUp = new LinkedList<>(rides);
        List<RideToProcess> passengersInCar = new LinkedList<>();
        int maxCapacity = driver.passenger();

        // add "Central Hub" as the first stop with no pickups or drop-offs
        stoppingPointRepository.findByName("Central Hub").ifPresent(sp -> RouteStops.add(new RouteStoppingPoint(sp, RouteStoppingStatus.NOT_REACHED, new ArrayList<>(), new ArrayList<>())));

        while (!ridesToPickUp.isEmpty() || !passengersInCar.isEmpty()) {
            Map<String, Object> nextStopResult = findClosestNextStop(ridesToPickUp, passengersInCar, currentStop, graph, maxCapacity);
            String nextStopName = (String) nextStopResult.get("nextStopName");
            RideToProcess associatedRide = (RideToProcess) nextStopResult.get("associatedRide");
            String stopType = (String) nextStopResult.get("stopType");

            if (nextStopName == null) {
                logger.info("No reachable pickup or drop-off points found from {}. Terminating route planning for driver {}.", currentStop, driver.id());
                break;
            }

            Map<String, Object> pathResult = dijkstra(graph, currentStop, nextStopName);
            List<String> pathToNextStop = (List<String>) pathResult.get("path");

            if (pathToNextStop == null || pathToNextStop.isEmpty()) {
                logger.warn("A path could not be found to {}. Skipping.", nextStopName);
                currentStop = nextStopName; // attempt to continue from the unreachable point
                continue;
            }

            // add the path to the route stops
            for (int i = 1; i < pathToNextStop.size(); i++) {
                String stopName = pathToNextStop.get(i);
                stoppingPointRepository.findByName(stopName).ifPresent(point -> RouteStops.add(new RouteStoppingPoint(point, RouteStoppingStatus.NOT_REACHED, new ArrayList<>(), new ArrayList<>())));
            }

            // get the last stop to add pickups/drop-offs
            RouteStoppingPoint lastStop = RouteStops.getLast();

            // create a new list for pickups/drop-offs and add the new ride
            List<String> updatedPickups = new ArrayList<>(lastStop.pickups());
            List<String> updatedDropOffs = new ArrayList<>(lastStop.dropOffs());

            // update the lists based on the stop type
            if ("pickup".equals(stopType)) {
                updatedPickups.add(associatedRide.customerId());
                passengersInCar.add(associatedRide);
                ridesToPickUp.remove(associatedRide);
            } else if ("dropOff".equals(stopType)) {
                updatedDropOffs.add(associatedRide.customerId());
                passengersInCar.remove(associatedRide);
            }

            // create a new RouteStoppingPoint record and replace the old one
            RouteStoppingPoint updatedStop = new RouteStoppingPoint(lastStop.stoppingPoint(), RouteStoppingStatus.NOT_REACHED, updatedPickups, updatedDropOffs);
            RouteStops.set(RouteStops.size() - 1, updatedStop);

            // update the current stop for the next iteration
            currentStop = nextStopName;
        }

        // plan the final path back to "Central Hub" if the last stop is not Central Hub
        if (!currentStop.equals("Central Hub")) {
            Map<String, Object> pathBackToCentralHub = dijkstra(graph, currentStop, "Central Hub");
            List<String> centralePath = (List<String>) pathBackToCentralHub.get("path");
            if (centralePath != null) {
                for (int i = 1; i < centralePath.size(); i++) {
                    String stopName = centralePath.get(i);
                    stoppingPointRepository.findByName(stopName).ifPresent(point -> RouteStops.add(new RouteStoppingPoint(point, RouteStoppingStatus.NOT_REACHED, new ArrayList<>(), new ArrayList<>())));
                }
            }
        }

        // after combining rides, save the final route to get its ID
        if (RouteStops.isEmpty()) {
            return null;
        }

        Route combinedRoute = new Route(null, driver.id(), RouteStatus.PLANNED, RouteStops);
        Route savedRoute = routeRepository.save(combinedRoute);

        // save processed rides with the correct RouteId
        List<RideToProcess> allProcessedRides = new LinkedList<>(rides);
        for (RideToProcess ride : allProcessedRides) {
            rideToProcessRepository.delete(ride);
            rideProcessedRepository.save(new RideProcessed(ride, savedRoute.id()));
        }

        return savedRoute;
    }

    private Map<String, Object> findClosestNextStop(List<RideToProcess> ridesToPickUp, List<RideToProcess> passengersInCar, String currentStop, Map<String, Map<String, Integer>> graph, int maxCapacity) {
        String closestStopName = null;
        RideToProcess associatedRide = null;
        String stopType = null;
        int minDistance = Integer.MAX_VALUE;

        // check for the closest ride to pick up if there's capacity
        if (passengersInCar.size() < maxCapacity) {
            for (RideToProcess ride : ridesToPickUp) {
                Map<String, Object> result = dijkstra(graph, currentStop, ride.start());
                Integer distance = (Integer) result.get("time");
                if (distance != null && distance < minDistance) {
                    minDistance = distance;
                    closestStopName = ride.start();
                    associatedRide = ride;
                    stopType = "pickup";
                }
            }
        }

        // check for the closest drop-off point
        for (RideToProcess ride : passengersInCar) {
            Map<String, Object> result = dijkstra(graph, currentStop, ride.end());
            Integer distance = (Integer) result.get("time");
            if (distance != null && distance < minDistance) {
                minDistance = distance;
                closestStopName = ride.end();
                associatedRide = ride;
                stopType = "dropOff";
            }
        }

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("nextStopName", closestStopName);
        finalResult.put("associatedRide", associatedRide);
        finalResult.put("stopType", stopType);

        return finalResult;
    }

    private Map<String, Object> dijkstra(Map<String, Map<String, Integer>> graph, String start, String end) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        Set<String> settled = new HashSet<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        distances.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            String currentNode = pq.poll();

            if (currentNode.equals(end)) {
                break; // path found
            }

            if (settled.contains(currentNode)) {
                continue;
            }

            settled.add(currentNode);

            if (graph.containsKey(currentNode)) {
                for (Map.Entry<String, Integer> neighborEntry : graph.get(currentNode).entrySet()) {
                    String neighbor = neighborEntry.getKey();
                    int weight = neighborEntry.getValue();
                    if (!settled.contains(neighbor)) {
                        int newDistance = distances.get(currentNode) + weight;
                        if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                            distances.put(neighbor, newDistance);
                            predecessors.put(neighbor, currentNode);
                            pq.add(neighbor);
                        }
                    }
                }
            }
        }

        // reconstruct path
        List<String> path = new LinkedList<>();
        String step = end;
        if (predecessors.get(step) == null && !start.equals(end)) {
            // path not found
            logger.warn("No path found from {} to {}.", start, end);
            Map<String, Object> result = new HashMap<>();
            result.put("path", null);
            result.put("time", 0);
            return result;
        }

        while (step != null) {
            path.addFirst(step);
            step = predecessors.get(step);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("time", distances.get(end));
        return result;
    }

    public int getRoutePickupWaitTime(RideProcessed ride) {
        // find the planned route
        Optional<Route> routeOptional = routeRepository.findById(ride.RouteId());
        if (routeOptional.isEmpty()) {
            logger.warn("Route with ID {} not found.", ride.RouteId());
            return -1;
        }
        Route route = routeOptional.get();

        // get the pickup location directly from the provided ride object
        String pickupLocation = ride.start();
        String rideId = ride.id();

        // retrieve connections to build the graph for time calculation between stops
        List<Connection> connections = connectionRepository.findAll();
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        for (Connection connection : connections) {
            graph.computeIfAbsent(connection.start(), k -> new HashMap<>()).put(connection.end(), connection.averageTravelTimeMinutes());
            graph.computeIfAbsent(connection.end(), k -> new HashMap<>()).put(connection.start(), connection.averageTravelTimeMinutes());
        }

        int totalTime = 0;
        String previousStop = null;

        // iterate through the planned stops to find the pickup location
        for (RouteStoppingPoint stop : route.plannedStops()) {
            String currentStop = stop.stoppingPoint().name();

            if (previousStop != null) {
                Map<String, Object> pathResult = dijkstra(graph, previousStop, currentStop);
                Integer travelTime = (Integer) pathResult.get("time");
                if (travelTime != null) {
                    totalTime += travelTime;
                } else {
                    logger.warn("Path not found from {} to {}. Cannot calculate total time.", previousStop, currentStop);
                    return -1; // Path broken
                }
            }

            if (currentStop.equals(pickupLocation)) {
                logger.info("Estimated pickup time for ride ID {} on route ID {} is {} minutes.", rideId, ride.RouteId(), totalTime);
                return totalTime;
            }
            previousStop = currentStop;
        }

        logger.warn("Pickup location '{}' for ride ID {} was not found on route ID {}.", pickupLocation, rideId, ride.RouteId());
        return -1;
    }

    public int getRouteDropOffWaitTime(RideProcessed ride) {
        // find the existing planned route
        Optional<Route> routeOptional = routeRepository.findById(ride.RouteId());
        if (routeOptional.isEmpty()) {
            logger.warn("Route with ID {} not found.", ride.RouteId());
            return -1;
        }
        Route route = routeOptional.get();

        String rideId = ride.id();
        String customerDestination = ride.end();

        // retrieve connections to build the graph
        List<Connection> connections = connectionRepository.findAll();
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        for (Connection connection : connections) {
            graph.computeIfAbsent(connection.start(), k -> new HashMap<>()).put(connection.end(), connection.averageTravelTimeMinutes());
            graph.computeIfAbsent(connection.end(), k -> new HashMap<>()).put(connection.start(), connection.averageTravelTimeMinutes());
        }

        int totalTime = 0;
        String previousStop = null;
        boolean pastCurrentStop = false;

        // iterate through the planned stops to find the one that has been 'REACHED'
        for (RouteStoppingPoint stop : route.plannedStops()) {
            String currentRouteStop = stop.stoppingPoint().name();

            if (!pastCurrentStop && stop.StoppingPointStatus() == RouteStoppingStatus.REACHED) {
                // we've found the current stop, start calculating from the next stop
                pastCurrentStop = true;
                previousStop = currentRouteStop;
                continue;
            }

            if (pastCurrentStop) {
                if (previousStop != null) {
                    Map<String, Object> pathResult = dijkstra(graph, previousStop, currentRouteStop);
                    Integer travelTime = (Integer) pathResult.get("time");
                    if (travelTime != null) {
                        totalTime += travelTime;
                    } else {
                        logger.warn("Path not found from {} to {}. Cannot calculate total time.", previousStop, currentRouteStop);
                        return -1;
                    }
                }

                if (currentRouteStop.equals(customerDestination)) {
                    logger.info("Estimated travel time from current stop '{}' to destination '{}' for ride ID {} is {} minutes.",
                            previousStop, customerDestination, rideId, totalTime);
                    return totalTime;
                }
                previousStop = currentRouteStop;
            }
        }

        // handle cases where the current stop was not found or the destination is not on the route after the current stop
        if (!pastCurrentStop) {
            logger.warn("No 'REACHED' stop found on route {}. Cannot calculate travel time.", ride.RouteId());
        } else {
            logger.warn("Destination '{}' for ride ID {} was not found on route ID {} after the current stop.", customerDestination, rideId, ride.RouteId());
        }
        return -1;
    }

    public boolean hasCustomerBeenPickedUp(RideProcessed ride) {
        @SuppressWarnings("OptionalGetWithoutIsPresent") List<RouteStoppingPoint> plannedStops = routeRepository.findById(ride.RouteId()).get().plannedStops();
        // iterate through all the planned stopping points
        for (RouteStoppingPoint stop : plannedStops) {
            // check if the stop's status indicates a pickup has occurred AND
            // if the specific customer is in the list of pickups for this stop.
            if (stop.StoppingPointStatus() == RouteStoppingStatus.REACHED && stop.pickups().contains(ride.customerId())) {
                System.out.println("Customer with ID '" + ride.customerId() + "' was found at a stop with status ALREADY_PICKED_UP.");
                return true; // found the customer, no need to check further
            }
        }
        // if the loop completes without finding the customer, they have not been picked up yet.
        return false;
    }
}
