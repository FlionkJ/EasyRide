package eu.flionkj.easy_ride.data;

import eu.flionkj.easy_ride.data.repository.ConnectionRepository;
import eu.flionkj.easy_ride.data.repository.DriverRepository;
import eu.flionkj.easy_ride.data.repository.RideRepository;
import eu.flionkj.easy_ride.data.repository.StoppingPointRepository;
import eu.flionkj.easy_ride.domain.connection.Connection;
import eu.flionkj.easy_ride.domain.driver.Driver;
import eu.flionkj.easy_ride.domain.ride.RideToProcess;
import eu.flionkj.easy_ride.domain.stopping_points.StoppingPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * a class that is executed when the application starts.
 * it deletes all existing data and adds new test stopping points,
 * connections, rides, and drivers to test the functionality.
 */

@Component
public class CreatingTestData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CreatingTestData.class);

    private final StoppingPointRepository stoppingPointRepository;
    private final ConnectionRepository connectionRepository;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;

    public CreatingTestData(
            StoppingPointRepository stoppingPointRepository,
            ConnectionRepository connectionRepository,
            RideRepository rideRepository,
            DriverRepository driverRepository
    ) {
        this.stoppingPointRepository = stoppingPointRepository;
        this.connectionRepository = connectionRepository;
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting database seeding...");

        // delete all existing data to ensure a clean state
        rideRepository.deleteAll();
        connectionRepository.deleteAll();
        stoppingPointRepository.deleteAll();
        driverRepository.deleteAll();
        logger.info("All existing test data has been deleted.");

        // create and save test stopping points
        StoppingPoint hannover = new StoppingPoint("Hannover");
        StoppingPoint hamburg = new StoppingPoint("Hamburg");
        StoppingPoint berlin = new StoppingPoint("Berlin");

        stoppingPointRepository.save(hannover);
        stoppingPointRepository.save(hamburg);
        stoppingPointRepository.save(berlin);
        logger.info("Test stopping points have been created and saved.");

        // create and save test connections
        Connection hannoverHamburg = new Connection("Hannover", "Hamburg", 90);
        Connection hamburgBerlin = new Connection("Hamburg", "Berlin", 120);
        Connection hannoverBerlin = new Connection("Hannover", "Berlin", 180);

        connectionRepository.save(hannoverHamburg);
        connectionRepository.save(hamburgBerlin);
        connectionRepository.save(hannoverBerlin);
        logger.info("Test connections have been created and saved.");

        // create and save a test ride
        RideToProcess testRide = new RideToProcess(
                "Max Mustermann",
                "Hannover",
                "Berlin"
        );
        rideRepository.save(testRide);
        logger.info("A test ride has been created and saved.");

        // create and save test drivers with passenger capacity
        // the passenger capacity must not be 0.
        Driver hans = new Driver("Hans", 2);
        Driver petra = new Driver("Petra", 1);
        Driver lisa = new Driver("Lisa", 3);
        driverRepository.save(hans);
        driverRepository.save(petra);
        driverRepository.save(lisa);
        logger.info("Test drivers have been created and saved.");

        logger.info("Database seeding completed. The data is now ready.");
    }
}
