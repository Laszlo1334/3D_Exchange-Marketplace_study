package com.education.marketplace_3D.repository;

import com.education.marketplace_3D.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    Page<Order> findAllByCustomer_Email(String email, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"customer", "executor"})
    Page<Order> findAll (Specification<Order> spec, Pageable pageable);
}
