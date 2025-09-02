package eu.flionkj.easy_ride.domain.customer;

public record RegisterCustomerDto(
        RegisterCustomerResult status,
        String customerId
) {
}
