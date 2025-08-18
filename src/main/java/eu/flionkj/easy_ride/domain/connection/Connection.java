package eu.flionkj.easy_ride.domain.connection;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

@Document
@CompoundIndexes({
        @CompoundIndex(name = "start_end_index", def = "{'start' : 1, 'end' : 1}", unique = true)
})
public record Connection(
        String start,
        String end,
        int averageTravelTimeMinutes
) {
}
