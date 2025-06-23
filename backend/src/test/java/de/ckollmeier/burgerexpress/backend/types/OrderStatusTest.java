package de.ckollmeier.burgerexpress.backend.types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderStatus")
class OrderStatusTest {

    @Nested
    @DisplayName("isFinal()")
    class IsFinal {
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"APPROVED", "PAID", "DELIVERED", "CANCELLED"})
        @DisplayName("returns true for final statuses")
        void should_returnTrue_forFinalStatuses(OrderStatus status) {
            assertThat(status.isFinal()).isTrue();
        }
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"PENDING", "CHECKOUT", "APPROVING", "IN_PROGRESS", "READY"})
        @DisplayName("returns false for non-final statuses")
        void should_returnFalse_forNonFinalStatuses(OrderStatus status) {
            assertThat(status.isFinal()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("isImmutable()")
    class IsImmutable {
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"APPROVED", "PAID", "IN_PROGRESS", "READY", "DELIVERED", "CANCELLED"})
        @DisplayName("returns true for immutable statuses")
        void should_returnTrue_forImmutableStatuses(OrderStatus status) {
            assertThat(status.isImmutable()).isTrue();
        }
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"PENDING", "CHECKOUT", "APPROVING"})
        @DisplayName("returns false for mutable statuses")
        void should_returnFalse_forMutableStatuses(OrderStatus status) {
            assertThat(status.isImmutable()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("isKitchen()")
    class IsKitchen {
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"PAID", "IN_PROGRESS"})
        @DisplayName("returns true for kitchen statuses")
        void should_returnTrue_forKitchenStatuses(OrderStatus status) {
            assertThat(status.isKitchen()).isTrue();
        }
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"PENDING", "CHECKOUT", "APPROVING", "APPROVED", "READY", "DELIVERED", "CANCELLED"})
        @DisplayName("returns false for non-kitchen statuses")
        void should_returnFalse_forNonKitchenStatuses(OrderStatus status) {
            assertThat(status.isKitchen()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("isCashier()")
    class IsCashier {
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"READY", "DELIVERED"})
        @DisplayName("returns true for cashier statuses")
        void should_returnTrue_forCashierStatuses(OrderStatus status) {
            assertThat(status.isCashier()).isTrue();
        }
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"PENDING", "CHECKOUT", "APPROVING", "APPROVED", "PAID", "IN_PROGRESS", "CANCELLED"})
        @DisplayName("returns false for non-cashier statuses")
        void should_returnFalse_forNonCashierStatuses(OrderStatus status) {
            assertThat(status.isCashier()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("isCustomer()")
    class IsCustomer {
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"IN_PROGRESS", "READY"})
        @DisplayName("returns true for customer statuses")
        void should_returnTrue_forCustomerStatuses(OrderStatus status) {
            assertThat(status.isCustomer()).isTrue();
        }
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"PENDING", "CHECKOUT", "APPROVING", "APPROVED", "PAID", "DELIVERED", "CANCELLED"})
        @DisplayName("returns false for non-customer statuses")
        void should_returnFalse_forNonCustomerStatuses(OrderStatus status) {
            assertThat(status.isCustomer()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("getFinalStatuses()")
    class GetFinalStatuses {
        
        @Test
        @DisplayName("returns list of final statuses")
        void should_returnListOfFinalStatuses() {
            List<OrderStatus> finalStatuses = OrderStatus.getFinalStatuses();
            
            assertThat(finalStatuses).containsExactlyInAnyOrder(
                    OrderStatus.APPROVED,
                    OrderStatus.PAID,
                    OrderStatus.DELIVERED,
                    OrderStatus.CANCELLED
            );
        }
    }
    
    @Nested
    @DisplayName("getKitchenStatuses()")
    class GetKitchenStatuses {
        
        @Test
        @DisplayName("returns list of kitchen statuses")
        void should_returnListOfKitchenStatuses() {
            List<OrderStatus> kitchenStatuses = OrderStatus.getKitchenStatuses();
            
            assertThat(kitchenStatuses).containsExactlyInAnyOrder(
                    OrderStatus.PAID,
                    OrderStatus.IN_PROGRESS
            );
        }
    }
    
    @Nested
    @DisplayName("getCustomerStatuses()")
    class GetCustomerStatuses {
        
        @Test
        @DisplayName("returns list of customer statuses")
        void should_returnListOfCustomerStatuses() {
            List<OrderStatus> customerStatuses = OrderStatus.getCustomerStatuses();
            
            assertThat(customerStatuses).containsExactlyInAnyOrder(
                    OrderStatus.IN_PROGRESS,
                    OrderStatus.READY
            );
        }
    }
    
    @Nested
    @DisplayName("getCashierStatuses()")
    class GetCashierStatuses {
        
        @Test
        @DisplayName("returns list of cashier statuses")
        void should_returnListOfCashierStatuses() {
            List<OrderStatus> cashierStatuses = OrderStatus.getCashierStatuses();
            
            assertThat(cashierStatuses).containsExactlyInAnyOrder(
                    OrderStatus.READY,
                    OrderStatus.DELIVERED
            );
        }
    }
    
    @Nested
    @DisplayName("getImmutableStatuses()")
    class GetImmutableStatuses {
        
        @Test
        @DisplayName("returns list of immutable statuses")
        void should_returnListOfImmutableStatuses() {
            List<OrderStatus> immutableStatuses = OrderStatus.getImmutableStatuses();
            
            assertThat(immutableStatuses).containsExactlyInAnyOrder(
                    OrderStatus.APPROVED,
                    OrderStatus.PAID,
                    OrderStatus.IN_PROGRESS,
                    OrderStatus.READY,
                    OrderStatus.DELIVERED,
                    OrderStatus.CANCELLED
            );
        }
    }
    
    @Nested
    @DisplayName("advancedStatus()")
    class AdvancedStatus {
        
        @Test
        @DisplayName("returns next status for PENDING")
        void should_returnCheckout_forPending() {
            assertThat(OrderStatus.PENDING.advancedStatus()).isEqualTo(OrderStatus.CHECKOUT);
        }
        
        @Test
        @DisplayName("returns next status for CHECKOUT")
        void should_returnApproving_forCheckout() {
            assertThat(OrderStatus.CHECKOUT.advancedStatus()).isEqualTo(OrderStatus.APPROVING);
        }
        
        @Test
        @DisplayName("returns next status for APPROVING")
        void should_returnApproved_forApproving() {
            assertThat(OrderStatus.APPROVING.advancedStatus()).isEqualTo(OrderStatus.APPROVED);
        }
        
        @Test
        @DisplayName("returns next status for APPROVED")
        void should_returnPaid_forApproved() {
            assertThat(OrderStatus.APPROVED.advancedStatus()).isEqualTo(OrderStatus.PAID);
        }
        
        @Test
        @DisplayName("returns next status for PAID")
        void should_returnInProgress_forPaid() {
            assertThat(OrderStatus.PAID.advancedStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        }
        
        @Test
        @DisplayName("returns next status for IN_PROGRESS")
        void should_returnReady_forInProgress() {
            assertThat(OrderStatus.IN_PROGRESS.advancedStatus()).isEqualTo(OrderStatus.READY);
        }
        
        @Test
        @DisplayName("returns next status for READY")
        void should_returnDelivered_forReady() {
            assertThat(OrderStatus.READY.advancedStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
        
        @Test
        @DisplayName("returns same status for DELIVERED")
        void should_returnDelivered_forDelivered() {
            assertThat(OrderStatus.DELIVERED.advancedStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
        
        @Test
        @DisplayName("returns same status for CANCELLED")
        void should_returnCancelled_forCancelled() {
            assertThat(OrderStatus.CANCELLED.advancedStatus()).isEqualTo(OrderStatus.CANCELLED);
        }
    }
}