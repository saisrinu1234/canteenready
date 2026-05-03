package com.example.canteen.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserEmail(String userEmail);
    List<Order> findByUserEmailAndServedFalse(String userEmail);
    List<Order> findByServedFalse();
}