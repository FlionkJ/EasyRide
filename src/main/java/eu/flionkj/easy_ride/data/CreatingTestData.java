package eu.flionkj.easy_ride.data;

import eu.flionkj.easy_ride.data.repository.*;
import eu.flionkj.easy_ride.domain.connection.Connection;
import eu.flionkj.easy_ride.domain.customer.Customer;
import eu.flionkj.easy_ride.domain.driver.Driver;
import eu.flionkj.easy_ride.domain.ride.RideToProcess;
import eu.flionkj.easy_ride.domain.stopping_points.StoppingPoint;
import eu.flionkj.easy_ride.service.RoutePlanningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class that is executed when the application starts.
 * It deletes all existing data and adds new test stopping points,
 * connections, rides, and drivers to test the functionality.
 */

@Component
public class CreatingTestData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CreatingTestData.class);
    private final Random random = new Random();

    // define the data to be created for testing
    private static final int NUMBER_OF_CUSTOMERS = 60;
    private static final int NUMBER_OF_DRIVERS = 10;
    // inclusive
    private static final int MIN_CAPACITY = 2;
    // exclusive
    private static final int MAX_CAPACITY = 4;
    private static final int NUMBER_OF_STOPPING_POINTS = 10;
    private static final double RANDOM_CONNECTION_PROBABILITY = 0.5;

    // repositories for database interaction
    private final StoppingPointRepository stoppingPointRepository;
    private final ConnectionRepository connectionRepository;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;
    private final RouteRepository routeRepository;
    private final RideProcessedRepository rideProcessedRepository;

    // service for triggering the route planning
    private final RoutePlanningService routePlanningService;

    // dependency injection via constructor
    public CreatingTestData(
            StoppingPointRepository stoppingPointRepository,
            ConnectionRepository connectionRepository,
            RideRepository rideRepository,
            DriverRepository driverRepository,
            CustomerRepository customerRepository, RouteRepository routeRepository, RideProcessedRepository rideProcessedRepository,
            RoutePlanningService routePlanningService
    ) {
        this.stoppingPointRepository = stoppingPointRepository;
        this.connectionRepository = connectionRepository;
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.customerRepository = customerRepository;
        this.routeRepository = routeRepository;
        this.rideProcessedRepository = rideProcessedRepository;
        this.routePlanningService = routePlanningService;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting to delete and recreate test data...");

        // delete all old data from all repositories to ensure a clean slate
        stoppingPointRepository.deleteAll();
        connectionRepository.deleteAll();
        rideRepository.deleteAll();
        driverRepository.deleteAll();
        customerRepository.deleteAll();
        rideProcessedRepository.deleteAll();
        routeRepository.deleteAll();

        // create stopping points
        // 'Central Hub' is a special hub
        List<String> stoppingPointNames = new ArrayList<>();
        String centralHub = "Central Hub";
        stoppingPointNames.add(centralHub);
        stoppingPointRepository.save(new StoppingPoint(centralHub));

        for (int i = 1; i < NUMBER_OF_STOPPING_POINTS; i++) {
            String name = "Stopping Point " + (char) ('A' + i - 1);
            stoppingPointNames.add(name);
            stoppingPointRepository.save(new StoppingPoint(name));
        }
        logger.info("Created {} test stopping points, including 'Central Hub'.", NUMBER_OF_STOPPING_POINTS);

        // create connections to form a complex, non-fully-connected graph
        List<String> otherStoppingPoints = stoppingPointNames.stream()
                .filter(s -> !s.equals(centralHub))
                .toList();

        // ensure 'Central Hub' is connected to every other stopping point for reachability
        for (String otherPoint : otherStoppingPoints) {
            int travelTime = 10 + random.nextInt(40);
            // save connections in both directions to simulate an undirected graph
            connectionRepository.save(new Connection(centralHub, otherPoint, travelTime));
        }

        // create random connections between other stopping points with a certain probability
        for (int i = 0; i < otherStoppingPoints.size(); i++) {
            for (int j = i + 1; j < otherStoppingPoints.size(); j++) {
                // add a connection with a certain probability to make the graph more complex
                if (random.nextDouble() < RANDOM_CONNECTION_PROBABILITY) {
                    String start = otherStoppingPoints.get(i);
                    String end = otherStoppingPoints.get(j);
                    int travelTime = 10 + random.nextInt(40);
                    connectionRepository.save(new Connection(start, end, travelTime));
                }
            }
        }
        logger.info("Created connections for a complex, non-fully-connected graph.");

        // create a defined number of drivers with varying capacities
        for (int i = 1; i <= NUMBER_OF_DRIVERS; i++) {
            String driverName = "Driver " + i;
            int capacity = MIN_CAPACITY + random.nextInt(MAX_CAPACITY);
            driverRepository.save(new Driver(null, driverName, capacity));
        }
        logger.info("Created {} test drivers with varying capacities.", NUMBER_OF_DRIVERS);

        // create a defined number of customers and their rides
        for (int i = 1; i <= NUMBER_OF_CUSTOMERS; i++) {
            String customerName = "Customer " + i;
            customerRepository.save(new Customer(customerName));

            String startPoint, endPoint;
            // ensure that rides do not start or end at 'Central Hub' and are not self-loops
            do {
                startPoint = stoppingPointNames.get(random.nextInt(stoppingPointNames.size()));
                endPoint = stoppingPointNames.get(random.nextInt(stoppingPointNames.size()));
            } while (startPoint.equals(endPoint) || startPoint.equals(centralHub) || endPoint.equals(centralHub));

            rideRepository.save(new RideToProcess(customerName, startPoint, endPoint));
        }
        logger.info("Created {} test customers and their rides.", NUMBER_OF_CUSTOMERS);

        // trigger the route planning service to process all new rides automatically
        routePlanningService.planRoutes();

        logger.info("Database seeding and initial route planning completed. The data is now ready.");
    }
}
