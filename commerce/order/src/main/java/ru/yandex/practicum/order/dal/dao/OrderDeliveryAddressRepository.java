package ru.yandex.practicum.order.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.order.dal.model.OrderDeliveryAddress;

import java.util.Optional;

public interface OrderDeliveryAddressRepository extends JpaRepository<OrderDeliveryAddress, String> {

    Optional<OrderDeliveryAddress> findByOrderId(String orderId);

}