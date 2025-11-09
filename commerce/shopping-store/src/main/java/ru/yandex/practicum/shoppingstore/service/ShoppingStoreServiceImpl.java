package ru.yandex.practicum.shoppingstore.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.shopping.store.dto.ProductDto;
import ru.yandex.practicum.api.shopping.store.dto.ProductRemoveRequestDto;
import ru.yandex.practicum.api.shopping.store.dto.SetProductQuantityStateRequestDto;
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
    public List<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByProductCategory(category, pageable).stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(String productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Товар c id = " + productId +" не найден"));
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Создание товара: {}", productDto);
        Product saved = productRepository.save(productMapper.toModel(productDto));
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар c id = " + productDto.getProductId() +" не найден"));
        productMapper.updateModel(productDto, product);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequestDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар c id = " + request.getProductId() +" не найден"));

        product.setQuantityState(request.getQuantityState());

        productRepository.save(product);

        return true;
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(ProductRemoveRequestDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар c id = " + request.getProductId() +" не найден"));

        product.setProductState(ProductState.DEACTIVATE);

        productRepository.save(product);

        return true;
    }
}
