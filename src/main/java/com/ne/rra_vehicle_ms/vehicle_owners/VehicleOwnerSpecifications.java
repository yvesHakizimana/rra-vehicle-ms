package com.ne.rra_vehicle_ms.vehicle_owners;

import org.springframework.data.jpa.domain.Specification;

import com.ne.rra_vehicle_ms.vehicles.Vehicle;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class VehicleOwnerSpecifications {
    // even though can be done with JPQL queries.
    public static Specification<VehicleOwner> withEmailContaining(String email){
        return (root, query, cb) ->
            email == null ? cb.conjunction() : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<VehicleOwner> withNationalIdContaining(String nationalId){
        return (root, query, cb) ->
                nationalId == null ? cb.conjunction() : cb.like(cb.lower(root.get("nationalId")), "%" + nationalId.toLowerCase() + "%");
    }

    public static Specification<VehicleOwner> withPhoneNumberContaining(String phoneNumber) {
        return (root, query, cb) ->
                phoneNumber == null ? cb.conjunction() : cb.like(cb.lower(root.get("phoneNumber")), "%" + phoneNumber.toLowerCase() + "%");
    }

    public static Specification<VehicleOwner> searchOwners(String query){
        return Specification.where(hasVehiclesSpec())
            .and((root, queryObj, cb) -> {
                if (query == null || query.isEmpty())
                    return cb.conjunction();
                return cb.or(
                    withEmailContaining(query).toPredicate(root, queryObj, cb),
                    withNationalIdContaining(query).toPredicate(root, queryObj, cb),
                    withPhoneNumberContaining(query).toPredicate(root, queryObj, cb)
                );
            });
    }

    public static Specification<VehicleOwner> hasVehiclesSpec() {
        return (root, query, cb) -> {
            query.distinct(true);
            
            // Use an inner join - this only returns owners with at least one vehicle
            Join<VehicleOwner, Vehicle> vehicleJoin = root.join("ownedVehicles", JoinType.INNER);
            
            // Just to make the query explicit, check that the vehicle ID is not null
            return cb.isNotNull(vehicleJoin.get("id"));
        };
    }
}