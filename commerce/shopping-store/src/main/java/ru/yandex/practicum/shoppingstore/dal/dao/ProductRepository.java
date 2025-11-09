package ru.yandex.practicum.shoppingstore.dal.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.api.shopping.cart.enums.ProductCategory;
import ru.yandex.practicum.shoppingstore.dal.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByProductCategory(ProductCategory category, Pageable pageable);
    
}