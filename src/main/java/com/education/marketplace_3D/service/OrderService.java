package com.education.marketplace_3D.service;

import com.education.marketplace_3D.dto.OrderCreateDto;
import com.education.marketplace_3D.dto.OrderResponseDto;
import com.education.marketplace_3D.dto.UserSummaryDto;
import com.education.marketplace_3D.entities.Order;
import com.education.marketplace_3D.entities.User;
import com.education.marketplace_3D.enums.OrderStatus;
import com.education.marketplace_3D.repository.OrderRepository;
import com.education.marketplace_3D.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.education.marketplace_3D.repository.specification.OrderSpecifications.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Transactional
    public UUID createOrder (OrderCreateDto dto, String customerEmail){
        User customer = userRepository.findByEmail(customerEmail).orElseThrow(EntityNotFoundException::new);
        Order order = new Order(dto.description(),dto.price(), customer, OrderStatus.CREATED, dto.deadline());
        orderRepository.save(order);
        transactionService.holdPayment(order);
        return order.getId();
    }

    @Transactional (readOnly = true)
    public Page<OrderResponseDto> getMyOrders (String userEmail, OrderStatus status, BigDecimal minPrice, Pageable pageable){
        Specification<Order> spec = Specification.where(hasCustomerEmail(userEmail))
                .and(hasStatus(status))
                .and(priceGreaterThanOrEqualTo(minPrice));
        return orderRepository.findAll(spec, pageable)
                .map(this::toDto);
    }

    @Transactional (readOnly = true)
    public Page<OrderResponseDto> getAvailableOrders(Pageable pageable){
        Specification<Order> spec = Specification.where(isAvailableExecution()).and(hasStatus(OrderStatus.CREATED));
        return orderRepository.findAll(spec,pageable)
                .map(this::toDto);
    }

//    @Transactional
//    public void takeOrder(UUID orderId, String executorEmail){
//        Order takenOrder = orderRepository.findById(orderId).orElseThrow(()-> new EntityNotFoundException("Not Found order"));
//        if(takenOrder.getStatus() != OrderStatus.CREATED) {
//            throw new IllegalStateException("Order is in progress or closed already");
//        }
//        User executor = userRepository.findByEmail(executorEmail).orElseThrow(()->new EntityNotFoundException("User not found"));
//        if(takenOrder.getCustomer().getId().equals(executor.getId())){
//            throw new IllegalArgumentException("You cannot take your own Order");
//        }
//        if(takenOrder.getExecutor() != null){
//            throw new IllegalArgumentException("Order already has executor");
//        }
//        takenOrder.setExecutor(executor);
//        takenOrder.setStatus(OrderStatus.IN_PROGRESS);
//    }

    @Transactional
    public OrderResponseDto completeOrder(UUID orderId, String userEmail){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found by ID"));
        if(!(order.getCustomer().getEmail().equals(userEmail))){
            throw new AccessDeniedException("Only customer can complete an order");
        }
        if(order.getStatus() != OrderStatus.IN_PROGRESS){
            throw new IllegalStateException("Order is not in progress status");
        }
        order.setStatus(OrderStatus.COMPLETED);
        order.setFinishedAt(LocalDateTime.now());
        transactionService.processPayment(order);
        return toDto(order);
    }

    @Transactional
    public OrderResponseDto takeOrder (UUID orderId, String executorEmail){
        Order takenOrder = orderRepository.findById(orderId).orElseThrow(()-> new EntityNotFoundException("Order not found by id"));
        User executor = userRepository.findByEmail(executorEmail).orElseThrow(()-> new EntityNotFoundException("Customer not found by email"));
        User customer = takenOrder.getCustomer();
        if(customer.getEmail().equals(executor.getEmail())){
            throw new IllegalArgumentException("Customer can not take his own order");
        }
        if(takenOrder.getStatus() != (OrderStatus.CREATED) || takenOrder.getExecutor() != null ){
            throw new IllegalStateException("Order status not valid or executor is not empty");
        }
        takenOrder.setExecutor(executor);
        takenOrder.setStatus(OrderStatus.IN_PROGRESS);
        return toDto(takenOrder);
    }

    @Transactional
    public OrderResponseDto cancelOrder (UUID orderId, String userEmail){
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new EntityNotFoundException("Order was not found by id"));
        if(!(order.getCustomer().getEmail().equals(userEmail))){
            throw new AccessDeniedException("Order can be cancelled only by its customer");
        }
        if(order.getStatus() != OrderStatus.CREATED){
            throw new IllegalStateException("Order can be cancelled only of its status is Created");
        }
        order.setStatus(OrderStatus.CANCELLED);
        transactionService.refundHoldPayment(order);
        return toDto(order);
    }

//    @Transactional (readOnly = true)
//    public void testJoinFetch(){
//        orderRepository.findAllOrdersWithCustomer();
//    }

    private OrderResponseDto toDto (Order order){
        UserSummaryDto customer = new UserSummaryDto(order.getCustomer().getId(), order.getCustomer().getDisplayName());
        //if order have not been taken by any of executor, this will help not to get nullPointerException
        UserSummaryDto executor = order.getExecutor() != null ? new UserSummaryDto(order.getExecutor().getId(), order.getExecutor().getDisplayName()) : null;
        return new OrderResponseDto(
                order.getId(),
                order.getDescription(),
                order.getPrice(),
                customer,
                executor,
                order.getStatus(),
                order.getCreatedAt(),
                order.getDeadline()
        );
    }

}
