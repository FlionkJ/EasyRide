package eu.flionkj.easy_ride.web.controller;

import eu.flionkj.easy_ride.web.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Server {

    private final PingService routePing;

    @Autowired
    public Server(PingService routePing) {
        this.routePing = routePing;
    }

    @GetMapping("/ping")
    public Record handlePingRoute() {
        return routePing.getPing();
    }
}
