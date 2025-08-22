package eu.flionkj.easy_ride.data;

import eu.flionkj.easy_ride.data.repository.*;
import eu.flionkj.easy_ride.domain.connection.Connection;
import eu.flionkj.easy_ride.domain.connection.CreateConnectionRequest;
import eu.flionkj.easy_ride.domain.customer.RegisterCustomerRequest;
import eu.flionkj.easy_ride.domain.customer.Customer;
import eu.flionkj.easy_ride.domain.driver.AddDriverRequest;
import eu.flionkj.easy_ride.domain.driver.Driver;
import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import eu.flionkj.easy_ride.domain.ride.RideToProcess;
import eu.flionkj.easy_ride.domain.route.Route;
import eu.flionkj.easy_ride.domain.route.RouteStatus;
import eu.flionkj.easy_ride.domain.route.RouteStoppingPoint;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointRequest;
import eu.flionkj.easy_ride.domain.stopping_points.StoppingPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class MongoDB {

    private static final Logger logger = LoggerFactory.getLogger(MongoDB.class);

    private final RideRepository rideRepository;
    private final StoppingPointRepository stoppingPointRepository;
    private final ConnectionRepository connectionRepository;
    private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;
    private final RouteRepository routeRepository;

    @Autowired
    public MongoDB(RideRepository rideRepository, StoppingPointRepository stoppingPointRepository, ConnectionRepository connectionRepository, DriverRepository driverRepository, CustomerRepository customerRepository, RouteRepository routeRepository) {
        this.rideRepository = rideRepository;
        this.stoppingPointRepository = stoppingPointRepository;
        this.connectionRepository = connectionRepository;
        this.driverRepository = driverRepository;
        this.customerRepository = customerRepository;
        this.routeRepository = routeRepository;
    }

    public void addRide(CreateRideRequest ride) {
        RideToProcess newRide = new RideToProcess(ride.id(), ride.start(), ride.end());
        rideRepository.save(newRide);
    }

    public void addStop(CreateStoppingPointRequest stoppingPoint) {
        StoppingPoint newStoppingPoint = new StoppingPoint(stoppingPoint.name());
        stoppingPointRepository.save(newStoppingPoint);
        logger.info("Stopping point {} was created successfully.", newStoppingPoint.name());
    }

    public boolean doesStoppingPointExist(String name) {
        return stoppingPointRepository.existsById(name);
    }

    public void addConnection(CreateConnectionRequest connection) {
        Connection newConnection = new Connection(connection.start(), connection.end(), connection.averageTravelTimeMinutes());
        connectionRepository.save(newConnection);
        logger.info("Connection from {} to {} with average travel time: {} ,was created successfully.", connection.start(), connection.end(), newConnection.averageTravelTimeMinutes());
    }

    public boolean doesConnectionExist(String start, String end) {
        return connectionRepository.existsByStartAndEnd(start, end);
    }

    public void addDriver(AddDriverRequest driver) {
        Driver newDriver = new Driver(null, driver.name(), driver.passenger());
        driverRepository.save(newDriver);
    }

    public boolean doesDriverExist(String driverId) {
        return driverRepository.existsById(driverId);
    }

    public void addCustomer(RegisterCustomerRequest customer) {
        Customer newCustomer = new Customer(customer.name());
        customerRepository.save(newCustomer);
    }

    public List<Route> getRoutesByDriverId(String driverId) {
        return routeRepository.findByDriverId(driverId);
    }

    public boolean doesRouteExist(String routeId) {
        return routeRepository.existsById(routeId);
    }

    public void updateRouteStatus(String routeId, RouteStatus newStatus) {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);

        if (optionalRoute.isEmpty()) {
            logger.warn("Route with ID {} not found. Cannot update status.", routeId);
            return;
        }

        Route existingRoute = optionalRoute.get();

        Route updatedRoute = new Route(
                existingRoute.id(),
                existingRoute.driverId(),
                newStatus,
                existingRoute.plannedStops()
        );

        routeRepository.save(updatedRoute);
        logger.info("Route with ID {} status updated to {}.", routeId, newStatus);
    }

    public Optional<RouteStoppingPoint> findFirstStoppingPointOfRoute(String tripId) {
        Optional<Route> optionalRoute = routeRepository.findById(tripId);

        if (optionalRoute.isEmpty()) {
            return Optional.empty();
        }

        Route route = optionalRoute.get();

        if (route.plannedStops().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(route.plannedStops().getFirst());
    }
}
