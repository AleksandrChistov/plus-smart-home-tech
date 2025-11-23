package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.dto.*;
import ru.yandex.practicum.api.warehouse.error.InsufficientItemDto;
import ru.yandex.practicum.api.warehouse.error.InsufficientStockError;
import ru.yandex.practicum.api.warehouse.error.ProductAlreadyExistError;
import ru.yandex.practicum.warehouse.dal.dao.WarehouseOrderRepository;
import ru.yandex.practicum.warehouse.dal.dao.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.dal.dao.WarehouseStockRepository;
import ru.yandex.practicum.warehouse.dal.model.WarehouseOrder;
import ru.yandex.practicum.warehouse.dal.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.dal.model.WarehouseStock;
import ru.yandex.practicum.warehouse.mapper.WarehouseProductMapper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseProductServiceImpl implements WarehouseProductService {

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    private final WarehouseProductRepository warehouseProductRepository;

    private final WarehouseStockRepository warehouseStockRepository;

    private final WarehouseOrderRepository warehouseOrderRepository;

    private final WarehouseProductMapper warehouseProductMapper;

    @Override
    public void addProduct(NewProductInWarehouseRequest newProductRequest) throws ProductAlreadyExistError {
        log.info("Добавление товара на склад {}", newProductRequest);

        boolean isExist = warehouseProductRepository.existsByProductId(newProductRequest.getProductId());

        if (isExist) {
            throw new ProductAlreadyExistError(newProductRequest.getProductId());
        }

        WarehouseProduct warehouseProduct = warehouseProductMapper.toModel(newProductRequest);

        log.info("Преобразованные данные для добавления товара на склад {}", warehouseProduct);

        warehouseProductRepository.save(warehouseProduct);

        log.info("Товар успешно добавлен на склад {}", warehouseProduct.getProductId());
    }

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto request) throws InsufficientStockError {
        log.info("Проверка товаров на складе {}", request);

        Map<String, Integer> requestProducts = request.getProducts();

        List<WarehouseProduct> products = warehouseProductRepository.findAllWithStock(requestProducts.keySet());

        return checkAndBook(requestProducts, products);
    }

    @Override
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) throws InsufficientStockError {
        log.info("Сборка товаров для заказа на складе {}", request);
        Map<String, Integer> requestProducts = request.getProducts();
        List<WarehouseProduct> dbProducts = warehouseProductRepository.findAllWithStock(requestProducts.keySet());

        BookedProductsDto bookedProductsDto = checkAndBook(requestProducts, dbProducts);

        log.info("Товар был проверен, изменяем кол-во на складе {}", request);

        warehouseStockRepository.saveAll(dbProducts.stream().map(p -> {
            int quantity = p.getQuantity() - requestProducts.get(p.getProductId());
            return new WarehouseStock(p.getProductId(), quantity);
        }).toList());


        WarehouseOrder warehouseOrder = WarehouseOrder.builder()
                .orderId(request.getOrderId())
                .productIds(requestProducts.keySet())
                .build();

        log.info("Сохраняем заказ на складе {}", warehouseOrder);

        warehouseOrderRepository.save(warehouseOrder);

        return bookedProductsDto;
    }

    private BookedProductsDto checkAndBook(Map<String, Integer> requestProducts, List<WarehouseProduct> products) throws InsufficientStockError {
        if (requestProducts.size() != products.size()) {
            throw new NotFoundException("Товар не найден на складе, проверьте ID товаров " + requestProducts.keySet());
        }

        List<InsufficientItemDto> insufficientItems = new ArrayList<>();
        float deliveryWeight = 0;
        float deliveryVolume = 0;
        boolean fragile = false;

        for (WarehouseProduct p : products) {
            Integer requestQuantity = requestProducts.get(p.getProductId());

            if (p.getQuantity() < requestQuantity) {
                insufficientItems.add(
                        InsufficientItemDto.builder()
                                .productId(p.getProductId())
                                .requestedQuantity(requestQuantity)
                                .availableQuantity(p.getQuantity())
                                .build()
                );
            } else {
                deliveryWeight += (requestQuantity * p.getWeight());
                deliveryVolume += (requestQuantity * p.getWidth() * p.getHeight() * p.getDepth());
                fragile = fragile || p.isFragile();
            }
        }

        if (insufficientItems.size() > 0) {
            throw new InsufficientStockError(insufficientItems);
        }

        return BookedProductsDto.builder()
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();
    }

    @Override
    public void shipProducts(ShippedToDeliveryRequest request) throws NotFoundException {
        log.info("Отправка товаров к заказу = {} на доставку {}", request.getOrderId(), request.getDeliveryId());
        WarehouseOrder warehouseOrder = warehouseOrderRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        warehouseOrder.setDeliveryId(request.getDeliveryId());

        warehouseOrderRepository.save(warehouseOrder);
    }

    @Override
    public void addQuantity(AddProductToWarehouseRequest addProductRequest) throws NotFoundException {
        log.info("Новое кол-во товара для обновления {} = {}", addProductRequest.getProductId(), addProductRequest.getQuantity());

        WarehouseStock warehouseStock = warehouseStockRepository.findByProductId(addProductRequest.getProductId())
                .orElseThrow(() -> new NotFoundException("Товар не найден, id = " + addProductRequest.getProductId()));

        warehouseStock.setQuantity(addProductRequest.getQuantity());

        warehouseStockRepository.save(warehouseStock);

        log.info("Кол-во {} товара {} успешно обновлен", warehouseStock.getQuantity(), warehouseStock.getProductId());
    }

    @Override
    public void returnProducts(Map<String, Integer> products) {
        log.info("Возврат товаров: {}", products);

        List<WarehouseStock> dbStocks = warehouseStockRepository.findAllByProductIdIn(products.keySet());

        List<WarehouseStock> changedStocks = dbStocks.stream().map(s -> {
            int quantity = s.getQuantity() + products.get(s.getProductId());
            return new WarehouseStock(s.getProductId(), quantity);
        }).toList();

        warehouseStockRepository.saveAll(changedStocks);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }
}
