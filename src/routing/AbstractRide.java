package routing;

public abstract class AbstractRide {
    private String startingPoint;
    private String destinationPoint;
    private String passengerName;

    public AbstractRide(String startingPoint, String destinationPoint, String passengerName) {
        this.startingPoint = startingPoint;
        this.destinationPoint = destinationPoint;
        this.passengerName = passengerName;
    }

    @Override
    public String toString() {
        return "Fahrgast: " + passengerName + ", von: " + startingPoint + ", nach: " + destinationPoint;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }

    public String getPassengerName() {
        return passengerName;
    }

}
