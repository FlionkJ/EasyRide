package eu.flionkj.easy_ride.data;

import eu.flionkj.easy_ride.routing.AbstractRide;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DB {
    private List<AbstractRide> ridesToProcess = new ArrayList<>();

    public void addRide(AbstractRide ride) {
        ridesToProcess.add(ride);
    }

    public List<AbstractRide> getRides() {
        return ridesToProcess;
    }

    public List<AbstractRide> getRidesAndClear() {
        List<AbstractRide> rides = ridesToProcess;
        ridesToProcess = new ArrayList<>();
        return rides;
    }
}
