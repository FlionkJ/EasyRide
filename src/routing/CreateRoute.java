package routing;

import data.DB;

public class CreateRoute {

    private DB db;

    public CreateRoute(DB database) {
        this.db = database;
    }

    public void createNewRoute() {
        db.getRidesAndClear();
    }
}
