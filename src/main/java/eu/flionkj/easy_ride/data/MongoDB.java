package eu.flionkj.easy_ride.data;

import eu.flionkj.easy_ride.data.repository.ConnectionRepository;
import eu.flionkj.easy_ride.data.repository.RideRepository;
import eu.flionkj.easy_ride.data.repository.StoppingPointRepository;
import eu.flionkj.easy_ride.domain.connection.Connection;
import eu.flionkj.easy_ride.domain.connection.CreateConnectionRequest;
import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import eu.flionkj.easy_ride.domain.ride.RideToProcess;
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

    @Autowired
    public MongoDB(RideRepository rideRepository, StoppingPointRepository stoppingPointRepository, ConnectionRepository connectionRepository) {
        this.rideRepository = rideRepository;
        this.stoppingPointRepository = stoppingPointRepository;
        this.connectionRepository = connectionRepository;
    }

    public void addRide(CreateRideRequest request) {
        RideToProcess newRide = new RideToProcess(request.name(), request.startingPoint(), request.destinationPoint());

        rideRepository.save(newRide);
        logger.info("Ride successfully saved in MongoDB.");
    }

    public List<RideToProcess> getRidesAndClear() {
        List<RideToProcess> rides = rideRepository.findAll();

        rideRepository.deleteAll();

        logger.info("{} Rides successfully deleted in MongoDB.", rides.size());

        return rides;
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
}
