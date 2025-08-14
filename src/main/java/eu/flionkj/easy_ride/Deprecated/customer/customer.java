package eu.flionkj.easy_ride.Deprecated.customer;

import eu.flionkj.easy_ride.routing.AbstractRide;
import eu.flionkj.easy_ride.routing.TaxiRide;

public class customer {

    private String name;

    public customer(String name) {
        this.name = name;
    }

    public AbstractRide createRide(String startingPoint, String destinationPoint) {
        return new TaxiRide(startingPoint, destinationPoint, name);
    }

}
