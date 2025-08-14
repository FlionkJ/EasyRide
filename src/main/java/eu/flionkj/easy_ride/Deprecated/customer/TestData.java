package eu.flionkj.easy_ride.Deprecated.customer;

import eu.flionkj.easy_ride.data.DB;

public class TestData {
    private DB db;
    private customer customer = new customer("Jeremias");

    public TestData(DB database) {
        this.db = database;
        var newRide = customer.createRide( "Hannover", "Hamburg");
        var newRide1 = customer.createRide("München", "Berlin");
        var newRide2 = customer.createRide( "Bremen", "Köln");
        var newRide3 = customer.createRide( "See", "Meer");
        db.addRide(newRide);
        db.addRide(newRide1);
        db.addRide(newRide2);
        db.addRide(newRide3);
    }
}
