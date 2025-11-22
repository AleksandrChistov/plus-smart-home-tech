package ru.yandex.practicum.delivery.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.delivery.dal.model.Delivery;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    Optional<Delivery> findByOrderId(String orderId);
}