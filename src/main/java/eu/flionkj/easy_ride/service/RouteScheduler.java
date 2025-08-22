package eu.flionkj.easy_ride.service;

import eu.flionkj.easy_ride.domain.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RouteScheduler.class);

    private final RoutePlanningService routePlanningService;

    @Autowired
    public RouteScheduler(RoutePlanningService routePlanningService) {
        this.routePlanningService = routePlanningService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void processRoutes() {
        logger.info("Triggering automated route processing.");
        List<Route> processedRoutes = routePlanningService.planRoutes();
        logger.info("Route processing finished. {} new routes were planned.", processedRoutes.size());
    }
}
