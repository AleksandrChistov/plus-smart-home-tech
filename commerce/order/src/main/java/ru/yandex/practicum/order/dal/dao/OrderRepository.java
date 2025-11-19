package ru.yandex.practicum.order.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.order.dal.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUsername(String username);

}
