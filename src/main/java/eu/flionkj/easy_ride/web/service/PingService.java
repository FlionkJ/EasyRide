package eu.flionkj.easy_ride.web.service;

import eu.flionkj.easy_ride.domain.PingResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class PingService {

    public PingResponse getPing() {
        LocalDateTime now = LocalDateTime.now();
        long unixTimestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        return new PingResponse(now, unixTimestamp);
    }
}