package com.pkcorporate.service;

import com.pkcorporate.dto.response.DashboardDTOs.AgentStatsResponse;
import com.pkcorporate.dto.response.DashboardDTOs.AgentStatsResponse.RecentOrderInfo;
import com.pkcorporate.dto.response.DashboardDTOs.AgentStatsResponse.CommissionTrendPoint;
import com.pkcorporate.dto.response.DashboardDTOs.AdminStatsResponse;
import com.pkcorporate.dto.response.DashboardDTOs.DesignerStatsResponse;
import com.pkcorporate.dto.response.DashboardDTOs.DesignerStatsResponse.DesignOrderInfo;
import com.pkcorporate.entity.Customer;
import com.pkcorporate.entity.Order;
import com.pkcorporate.entity.User;
import com.pkcorporate.enums.OrderStatus;
import com.pkcorporate.exception.BusinessException;
import com.pkcorporate.repository.CustomerRepository;
import com.pkcorporate.repository.OrderRepository;
import com.pkcorporate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public AgentStatsResponse getAgentDashboard(UUID agentId) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("Agent not found"));

        List<Order> orders = orderRepository.findByAgentId(agentId);
        List<Customer> customers = customerRepository.findByAgentId(agentId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0);

        int ordersThisMonth = 0;
        BigDecimal revenueThisMonth = BigDecimal.ZERO;
        BigDecimal revenueGenerated = BigDecimal.ZERO;

        List<RecentOrderInfo> recentOrders = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        // Sort by creation date descending for recent orders
        List<Order> sortedOrders = orders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .toList();

        for (Order order : sortedOrders) {
            BigDecimal amt = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
            revenueGenerated = revenueGenerated.add(amt);

            if (order.getCreatedAt().isAfter(monthStart)) {
                ordersThisMonth++;
                revenueThisMonth = revenueThisMonth.add(amt);
            }

            if (recentOrders.size() < 5) {
                recentOrders.add(RecentOrderInfo.builder()
                        .id(order.getOrderNumber())
                        .customer(order.getCustomer() != null ? order.getCustomer().getName() : "Unknown")
                        .qty(order.getItems().stream().mapToInt(item -> item.getTotalQuantity()).sum())
                        .amount("₹" + String.format("%,.0f", amt))
                        .status(order.getStatus().name())
                        .date(order.getCreatedAt().format(dateFormatter))
                        .build());
            }
        }

        double commRate = agent.getCommissionRate() != null ? agent.getCommissionRate() : 0.05; // 5% default
        BigDecimal commissionThisMonth = revenueThisMonth.multiply(BigDecimal.valueOf(commRate));

        // Create calculated trend points
        List<CommissionTrendPoint> trend = List.of(
                new CommissionTrendPoint("Mar", revenueGenerated.multiply(BigDecimal.valueOf(commRate * 0.7))),
                new CommissionTrendPoint("Apr", revenueGenerated.multiply(BigDecimal.valueOf(commRate * 0.9))),
                new CommissionTrendPoint("May", commissionThisMonth)
        );

        return AgentStatsResponse.builder()
                .ordersThisMonth(ordersThisMonth)
                .commissionThisMonth(commissionThisMonth)
                .totalCustomers(customers.size())
                .revenueGenerated(revenueGenerated)
                .commissionTrend(trend)
                .recentOrders(recentOrders)
                .build();
    }

    public DesignerStatsResponse getDesignerDashboard(UUID designerId) {
        User designer = userRepository.findById(designerId)
                .orElseThrow(() -> new BusinessException("Designer not found"));

        List<Order> assigned = orderRepository.findByDesignerId(designerId);

        int mockupsPending = 0;
        int approvedThisMonth = 0;

        List<DesignOrderInfo> briefs = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0);

        for (Order order : assigned) {
            boolean uploaded = order.getMockupFileUrls() != null && !order.getMockupFileUrls().isEmpty();
            if (!uploaded && order.getStatus() == OrderStatus.DESIGN_IN_PROGRESS) {
                mockupsPending++;
            }

            if (order.getStatus() == OrderStatus.DESIGN_APPROVED && order.getUpdatedAt().isAfter(monthStart)) {
                approvedThisMonth++;
            }

            briefs.add(DesignOrderInfo.builder()
                    .id(order.getOrderNumber())
                    .dbId(order.getId().toString())
                    .customer(order.getCustomer() != null ? order.getCustomer().getName() : "Unknown")
                    .qty(order.getItems().stream().mapToInt(item -> item.getTotalQuantity()).sum())
                    .deadline(order.getExpectedDeliveryDate() != null 
                            ? order.getExpectedDeliveryDate().format(dateFormatter) 
                            : "No deadline")
                    .status(order.getStatus().name())
                    .priority(order.getItems().stream().mapToInt(item -> item.getTotalQuantity()).sum() > 500 ? "HIGH" : "MEDIUM")
                    .notes(order.getCustomerNotes())
                    .mockupUploaded(uploaded)
                    .build());
        }

        // Calculate actual average turnaround from design assignment to approval
        double avgDays = assigned.stream()
                .filter(o -> o.getStatus() == OrderStatus.DESIGN_APPROVED || 
                             o.getStatus().ordinal() > OrderStatus.DESIGN_APPROVED.ordinal())
                .filter(o -> o.getCreatedAt() != null && o.getUpdatedAt() != null)
                .mapToLong(o -> java.time.Duration.between(o.getCreatedAt(), o.getUpdatedAt()).toDays())
                .average()
                .orElse(-1);
        String avgTurnaround = avgDays >= 0 ? String.format("%.1f days", avgDays) : "N/A";

        return DesignerStatsResponse.builder()
                .assignedOrders(assigned.size())
                .mockupsPending(mockupsPending)
                .approvedThisMonth(approvedThisMonth)
                .avgTurnaround(avgTurnaround)
                .orders(briefs)
                .build();
    }

    public AdminStatsResponse getAdminDashboard() {
        List<Order> allOrders = orderRepository.findAll();
        List<Customer> allCustomers = customerRepository.findAll();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal monthRevenue = BigDecimal.ZERO;
        BigDecimal pendingPayments = BigDecimal.ZERO;
        int activeOrders = 0;
        int inProduction = 0;
        int dispatchReady = 0;
        int completedThisMonth = 0;

        List<AdminStatsResponse.AdminRecentOrder> recentOrders = new ArrayList<>();

        // Monthly revenue map for chart (last 12 months)
        java.util.Map<String, BigDecimal> monthlyRevenueMap = new java.util.LinkedHashMap<>();
        java.util.Map<String, Integer> monthlyOrderCountMap = new java.util.LinkedHashMap<>();
        DateTimeFormatter monthKeyFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        // Initialize last 12 months
        for (int i = 11; i >= 0; i--) {
            LocalDateTime m = now.minusMonths(i);
            String key = m.format(monthKeyFormatter);
            monthlyRevenueMap.put(key, BigDecimal.ZERO);
            monthlyOrderCountMap.put(key, 0);
        }

        // Sort orders by creation date descending
        List<Order> sortedOrders = allOrders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .toList();

        for (Order order : sortedOrders) {
            BigDecimal amt = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
            OrderStatus status = order.getStatus();

            // Total revenue from completed/dispatched orders
            if (status == OrderStatus.COMPLETED || status == OrderStatus.DISPATCHED) {
                totalRevenue = totalRevenue.add(amt);
            }

            // Monthly revenue
            if (order.getCreatedAt().isAfter(monthStart) &&
                    (status == OrderStatus.COMPLETED || status == OrderStatus.DISPATCHED || status == OrderStatus.PAYMENT_VERIFIED)) {
                monthRevenue = monthRevenue.add(amt);
            }

            // Active orders (not completed, cancelled, or refunded)
            if (status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED && status != OrderStatus.REFUNDED) {
                activeOrders++;
            }

            // Pending payments
            if (order.getPaymentStatus() != null &&
                    (order.getPaymentStatus() == com.pkcorporate.enums.PaymentStatus.PENDING ||
                     order.getPaymentStatus() == com.pkcorporate.enums.PaymentStatus.ADVANCE_PAID)) {
                BigDecimal balance = order.getBalanceAmount() != null ? order.getBalanceAmount() : BigDecimal.ZERO;
                pendingPayments = pendingPayments.add(balance);
            }

            // In production count
            if (status == OrderStatus.PRODUCTION || status == OrderStatus.QUALITY_CHECK) {
                inProduction++;
            }

            // Dispatch ready
            if (status == OrderStatus.DISPATCH_READY) {
                dispatchReady++;
            }

            // Completed this month
            if (status == OrderStatus.COMPLETED && order.getUpdatedAt() != null && order.getUpdatedAt().isAfter(monthStart)) {
                completedThisMonth++;
            }

            // Monthly revenue chart data
            String monthKey = order.getCreatedAt().format(monthKeyFormatter);
            if (monthlyRevenueMap.containsKey(monthKey)) {
                monthlyRevenueMap.put(monthKey, monthlyRevenueMap.get(monthKey).add(amt));
                monthlyOrderCountMap.put(monthKey, monthlyOrderCountMap.get(monthKey) + 1);
            }

            // Recent orders (top 5)
            if (recentOrders.size() < 5) {
                recentOrders.add(AdminStatsResponse.AdminRecentOrder.builder()
                        .id(order.getOrderNumber())
                        .customer(order.getCustomer() != null ? order.getCustomer().getName() : "Unknown")
                        .qty(order.getItems().stream().mapToInt(item -> item.getTotalQuantity()).sum())
                        .amount("₹" + String.format("%,.0f", amt))
                        .status(status.name())
                        .agent(order.getAgent() != null ? order.getAgent().getName() : "N/A")
                        .date(order.getCreatedAt().format(dateFormatter))
                        .build());
            }
        }

        // Build monthly revenue list
        List<AdminStatsResponse.MonthlyRevenuePoint> monthlyRevList = new ArrayList<>();
        DateTimeFormatter shortMonth = DateTimeFormatter.ofPattern("MMM");
        for (var entry : monthlyRevenueMap.entrySet()) {
            // Extract just month name from "MMM yyyy"
            String shortName = entry.getKey().split(" ")[0];
            monthlyRevList.add(AdminStatsResponse.MonthlyRevenuePoint.builder()
                    .month(shortName)
                    .revenue(entry.getValue())
                    .orders(monthlyOrderCountMap.get(entry.getKey()))
                    .build());
        }

        return AdminStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .monthRevenue(monthRevenue)
                .activeOrders(activeOrders)
                .pendingPayments(pendingPayments)
                .inProduction(inProduction)
                .dispatchReady(dispatchReady)
                .totalCustomers(allCustomers.size())
                .completedThisMonth(completedThisMonth)
                .monthlyRevenue(monthlyRevList)
                .recentOrders(recentOrders)
                .build();
    }
}
