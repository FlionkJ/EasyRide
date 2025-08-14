package eu.flionkj.easy_ride.routing;

public abstract class AbstractRide {
    private String passengerName;
    private String startingPoint;
    private String destinationPoint;

    public AbstractRide(String passengerName, String startingPoint, String destinationPoint) {
        this.passengerName = passengerName;
        this.startingPoint = startingPoint;
        this.destinationPoint = destinationPoint;
    }

    @Override
    public String toString() {
        return "Passenger: " + passengerName + ", Start Point: " + startingPoint + ", Destination Point: " + destinationPoint;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }

}
