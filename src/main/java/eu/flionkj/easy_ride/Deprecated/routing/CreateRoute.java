package eu.flionkj.easy_ride.Deprecated.routing;

import eu.flionkj.easy_ride.data.DB;

public class CreateRoute {

    private DB db;

    public CreateRoute(DB database) {
        this.db = database;
    }

    public void createNewRoute() {
        db.getRidesAndClear();
    }
}
