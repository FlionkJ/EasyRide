import customer.customer;
import data.DB;
import routing.CreateRoute;

public class Main {
    static DB DB = new DB();

    public static void main(String[] args) {
        new TestData(DB);
        CreateRoute routeCreator = new CreateRoute(DB);
        System.out.println("Alle Fahrten in der Datenbank:");
        System.out.println(DB.getRides());
        routeCreator.createNewRoute();
    }
}
