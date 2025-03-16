package com.webstore.repositories;

import com.webstore.model.entities.Order;
import com.webstore.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}
