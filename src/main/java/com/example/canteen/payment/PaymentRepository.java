package com.example.canteen.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.canteen.orders.Order;

public interface PaymentRepository extends JpaRepository<Payment,Long>{

    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

    Optional<Payment> findByOrder(Order order);
    

}
