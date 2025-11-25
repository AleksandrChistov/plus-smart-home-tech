package ru.yandex.practicum.shoppingstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.shared.error.ProductNotFoundException;
import ru.yandex.practicum.api.shopping.store.dto.*;
import ru.yandex.practicum.api.shopping.store.enums.ProductCategory;
import ru.yandex.practicum.api.shopping.store.enums.ProductState;
import ru.yandex.practicum.shoppingstore.dal.dao.ProductRepository;
import ru.yandex.practicum.shoppingstore.dal.model.Product;
import ru.yandex.practicum.shoppingstore.mapper.ProductMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = "products")
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    @Cacheable(
            key = "{#category, #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString()}",
            unless = "#result.getContent().size() < 10"
    )
    @Transactional(readOnly = true)
    public ProductDto getProductsByCategory(ProductCategory category, Pageable pageable) {
        log.info("Получение товаров по категории {} страницы {} и сортировкой {}",
                category, pageable.getPageNumber(), pageable.getSort());

        List<ProductContentDto> productDtos = productRepository.findByProductCategory(category, pageable).stream()
                .map(productMapper::toDto)
                .toList();

        List<SortDto> sortDtos = pageable.getSort().stream()
                .map(order -> new SortDto(order.getDirection().name(), order.getProperty()))
                .toList();

        return new ProductDto(productDtos, sortDtos);
    }

    @Override
    @Cacheable(
            key = "#productId",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public ProductContentDto getProductById(String productId) {
        log.info("Получение товара по id = {}", productId);
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException("Товар c id = " + productId + " не найден"));
    }

    @Override
    @CacheEvict(allEntries = true)
    public ProductContentDto createProduct(ProductContentDto productDto) {
        log.info("Создание товара: {}", productDto);

        Product saved = productRepository.save(productMapper.toModel(productDto));

        return productMapper.toDto(saved);
    }

    @Override
    @CacheEvict(allEntries = true)
    public ProductContentDto updateProduct(ProductContentDto productDto) {
        log.info("Обновление товара: {}", productDto);

        Product product = getOrElseThrow(productDto.getProductId());

        productMapper.updateModel(productDto, product);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean setProductQuantityState(SetProductQuantityStateRequestDto request) {
        log.info("Изменение кол-ва товара: {}", request);

        Product product = getOrElseThrow(request.getProductId());

        product.setQuantityState(request.getQuantityState());

        productRepository.save(product);

        return true;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeProductFromStore(ProductRemoveRequestDto request) {
        log.info("Удаление товара c id = {}", request.getProductId());

        Product product = getOrElseThrow(request.getProductId());

        product.setProductState(ProductState.DEACTIVATE);

        productRepository.save(product);

        return true;
    }

    private Product getOrElseThrow(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар c id = " + productId + " не найден"));
    }
}
