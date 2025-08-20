package eu.flionkj.easy_ride.data.repository;

import eu.flionkj.easy_ride.domain.customer.Customer;
import eu.flionkj.easy_ride.domain.driver.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByName(String name);
}
