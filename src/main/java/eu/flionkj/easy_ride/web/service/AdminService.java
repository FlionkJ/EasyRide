package eu.flionkj.easy_ride.web.service;

import com.mongodb.DuplicateKeyException;
import eu.flionkj.easy_ride.data.MongoDB;
import eu.flionkj.easy_ride.domain.stopping_points.CreateStoppingPointRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final MongoDB db;

    public AdminService(MongoDB db) {
        this.db = db;
    }

    public String createStoppingPoint(CreateStoppingPointRequest request) {
        // Validate request
        if (request.name() == null || request.name().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        try {
            db.addStop(request);
            return "Stopping point was created successfully.";
        } catch (DuplicateKeyException e) {
            return "A stopping point with this name already exists.";
        }
    }

}
