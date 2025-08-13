import customer.customer;
import data.DB;

public class TestData {
    private DB db;
    private customer customer = new customer();

    public TestData(DB database) {
        this.db = database;
        var newRide = customer.createRide("Jeremias", "Hannover", "Hamburg");
        var newRide1 = customer.createRide("Jeremias", "München", "Berlin");
        var newRide2 = customer.createRide("Jeremias", "Bremen", "Köln");
        var newRide3 = customer.createRide("Jeremias", "See", "Meer");
        db.addRide(newRide);
        db.addRide(newRide1);
        db.addRide(newRide2);
        db.addRide(newRide3);
    }
}
