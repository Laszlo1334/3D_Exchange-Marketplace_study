package com.education.marketplace_3D.repository.specification;

import com.education.marketplace_3D.entities.Order;
import com.education.marketplace_3D.entities.User;
import com.education.marketplace_3D.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSpecifications {
    public static Specification<Order> hasCustomerEmail(String email) {
        if(email == null){
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("customer").get("email"), email);
    }

    public static Specification<Order> hasStatus (OrderStatus status){
        if(status == null){
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Order> priceGreaterThanOrEqualTo(BigDecimal minPrice){
        if (minPrice == null){
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Order> isAvailableExecution(){
        return (root,query,cb) -> cb.isNull(root.get("executor"));
    }
}
