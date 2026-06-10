package com.pkcorporate.repository;

import com.pkcorporate.entity.Order;
import com.pkcorporate.entity.User;
import com.pkcorporate.enums.OrderStatus;
import com.pkcorporate.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByTrackingToken(String trackingToken);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
    Page<Order> findByAgentOrderByCreatedAtDesc(User agent, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Order> findByAgentId(UUID agentId);
    List<Order> findByDesignerId(UUID designerId);

    @Query("SELECT o FROM Order o WHERE o.agent = :agent AND o.status = :status")
    List<Order> findByAgentAndStatus(@Param("agent") User agent, @Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :start AND o.createdAt <= :end")
    Long countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' AND o.createdAt >= :start")
    BigDecimal sumRevenueFrom(@Param("start") LocalDateTime start);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    List<Order> findByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT o FROM Order o WHERE " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(o.customer.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(o.customer.company) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Order> searchOrders(@Param("q") String query, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status IN ('PAYMENT_VERIFIED', 'DESIGN_APPROVED') AND o.designer IS NULL")
    List<Order> findUnassignedDesignOrders();

    @Query("SELECT o FROM Order o WHERE o.designer.id = :designerId AND o.status = 'DESIGN_IN_PROGRESS'")
    List<Order> findDesignerActiveOrders(@Param("designerId") UUID designerId);
}
