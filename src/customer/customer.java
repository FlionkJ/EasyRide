package customer;

import routing.AbstractRide;
import routing.TaxiRide;

public class customer {

    private String rideID;

    public AbstractRide createRide(String customerName, String startingPoint, String destinationPoint) {
        return new TaxiRide(startingPoint, destinationPoint, customerName);
    }

    public void setRideID(String rideID) {
        this.rideID = rideID;
    }

    public Integer getEstimatedPickupTime() {
        return 10;
    }
}
