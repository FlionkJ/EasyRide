package eu.flionkj.easy_ride.domain;

import java.time.LocalDateTime;

public record PingResponse(LocalDateTime timestamp, long unixTimestamp) {
}
