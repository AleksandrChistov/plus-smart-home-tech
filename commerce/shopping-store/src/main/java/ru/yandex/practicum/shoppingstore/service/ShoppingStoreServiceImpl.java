package ru.yandex.practicum.shoppingstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.shared.error.NotFoundException;
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
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
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
    @Transactional(readOnly = true)
    public ProductContentDto getProductById(String productId) {
        log.info("Получение товара по id = {}", productId);
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Товар c id = " + productId +" не найден"));
    }

    @Override
    @Transactional
    public ProductContentDto createProduct(ProductContentDto productDto) {
        log.info("Создание товара: {}", productDto);
        Product saved = productRepository.save(productMapper.toModel(productDto));
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductContentDto updateProduct(ProductContentDto productDto) {
        log.info("Обновление товара: {}", productDto);
        Product product = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар c id = " + productDto.getProductId() +" не найден"));
        productMapper.updateModel(productDto, product);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequestDto request) {
        log.info("Изменение кол-ва товара: {}", request);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар c id = " + request.getProductId() +" не найден"));

        product.setQuantityState(request.getQuantityState());

        productRepository.save(product);

        return true;
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(ProductRemoveRequestDto request) {
        log.info("Удаление товара c id = {}", request.getProductId());
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар c id = " + request.getProductId() +" не найден"));

        product.setProductState(ProductState.DEACTIVATE);

        productRepository.save(product);

        return true;
    }
}
