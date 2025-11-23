package ru.yandex.practicum.warehouse.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.dal.model.WarehouseOrder;

import java.util.Collection;
import java.util.Optional;

public interface WarehouseOrderRepository extends JpaRepository<WarehouseOrder, String> {

    Optional<WarehouseOrder> findByOrderId(String orderId);

    void deleteAllByProductIdIn(Collection<String> productIds);
}