package com.example.canteen.orders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order saveOrder(Order order) {

        // 🔥 VERY IMPORTANT
        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
        }

        return orderRepository.save(order);
    }

    public List<Order> getPendingOrdersByUser(String email) {
        return orderRepository.findByUserEmailAndServedFalse(email);
    }

    public List<Order> getAllPendingOrders() {
        return orderRepository.findByServedFalse();
    }

    public Order markAsServed(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setServed(true);

        return orderRepository.save(order);
    }
}