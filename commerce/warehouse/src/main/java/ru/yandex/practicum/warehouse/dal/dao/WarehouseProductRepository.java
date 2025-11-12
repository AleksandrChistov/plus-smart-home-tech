package ru.yandex.practicum.warehouse.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.warehouse.dal.model.WarehouseProduct;

import java.util.Collection;
import java.util.List;

@Repository
public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, String> {

    boolean existsByProductId(String productId);

    @Query("SELECT wp FROM WarehouseProduct wp JOIN FETCH wp.stock WHERE wp.productId IN :productIds")
    List<WarehouseProduct> findAllWithStock(@Param("productIds") Collection<String> productIds);

}