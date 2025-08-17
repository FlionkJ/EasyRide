package eu.flionkj.easy_ride.data;

import eu.flionkj.easy_ride.data.repository.RideRepository;
import eu.flionkj.easy_ride.data.repository.StoppingPointRepository;
import eu.flionkj.easy_ride.domain.ride.CreateRideRequest;
import eu.flionkj.easy_ride.domain.ride.RideToProcess;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointRequest;
import eu.flionkj.easy_ride.domain.stopping_points.StoppingPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MongoDB {

    private static final Logger logger = LoggerFactory.getLogger(MongoDB.class);

    private final RideRepository rideRepository;
    private final StoppingPointRepository stoppingPointRepository;

    @Autowired
    public MongoDB(RideRepository rideRepository, StoppingPointRepository stoppingPointRepository) {
        this.rideRepository = rideRepository;
        this.stoppingPointRepository = stoppingPointRepository;
    }

    public void addRide(CreateRideRequest request) {
        RideToProcess newRide = new RideToProcess(
                request.name(),
                request.startingPoint(),
                request.destinationPoint()
        );

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
        logger.info("Stopping point %s was created successfully.", newStoppingPoint);
    }
}
