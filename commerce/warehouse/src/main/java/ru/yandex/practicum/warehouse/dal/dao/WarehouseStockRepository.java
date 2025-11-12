package ru.yandex.practicum.warehouse.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.warehouse.dal.model.WarehouseStock;

import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, String> {

    Optional<WarehouseStock> findByProductId(String productId);

}