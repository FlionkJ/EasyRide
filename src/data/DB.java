package data;

import routing.AbstractRide;

import java.util.ArrayList;
import java.util.List;

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
