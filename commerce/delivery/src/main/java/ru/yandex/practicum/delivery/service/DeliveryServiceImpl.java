package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.api.delivery.dto.DeliveryRequest;
import ru.yandex.practicum.api.delivery.enums.DeliveryState;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.order.dto.OrderRequest;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.delivery.client.order.OrderClient;
import ru.yandex.practicum.delivery.client.warehouse.WarehouseClient;
import ru.yandex.practicum.delivery.dal.dao.DeliveryRepository;
import ru.yandex.practicum.delivery.dal.model.Delivery;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
    
    private final DeliveryRepository deliveryRepository;

    private final DeliveryMapper deliveryMapper;

    private final OrderClient orderClient;

    private final WarehouseClient warehouseClient;

    private final BigDecimal BASE_DELIVERY_COST = BigDecimal.valueOf(5.0);
    private final BigDecimal CITY_MULTIPLIER = BigDecimal.TWO;
    private final BigDecimal FRAGILE_MULTIPLIER = BigDecimal.valueOf(0.2);
    private final BigDecimal WEIGHT_RATE = BigDecimal.valueOf(0.3);
    private final BigDecimal VOLUME_RATE = BigDecimal.valueOf(0.2);
    private final BigDecimal STREET_MULTIPLIER = BigDecimal.valueOf(0.2);

    @Override
    public DeliveryDto create(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toModel(deliveryDto);
        Delivery savedDelivery = deliveryRepository.saveAndFlush(delivery);
        return deliveryMapper.toDto(savedDelivery);
    }
    
    @Override
    public BigDecimal calculateTotal(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Доставка для заказа с id = " + orderDto.getOrderId() + " не найдена"));

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        BigDecimal total = BASE_DELIVERY_COST;

        if ("ADDRESS_2".equals(delivery.getFromCity())) {
            total = total.add(total.multiply(CITY_MULTIPLIER));
        }

        if (orderDto.getFragile()) {
            total = total.add(total.multiply(FRAGILE_MULTIPLIER));
        }

        total = total
                .add(BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(WEIGHT_RATE));

        total = total
                .add(BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(VOLUME_RATE));

        if (!warehouseAddress.getStreet().equals(delivery.getToStreet())) {
            total = total.add(total.multiply(STREET_MULTIPLIER));
        }

        return total;
    }
    
    @Override
    public void picked(DeliveryRequest request) {
        Delivery delivery = getDeliveryById(request.getDeliveryId());

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);

        deliveryRepository.save(delivery);

        OrderRequest orderRequest = new OrderRequest(delivery.getOrderId());

        // todo: uncomment when ready
//        warehouseClient.shippedToDelivery(delivery);

        orderClient.assembleOrder(orderRequest);
    }

    @Override
    public void successful(DeliveryRequest request) {
        Delivery delivery = getDeliveryById(request.getDeliveryId());

        delivery.setDeliveryState(DeliveryState.DELIVERED);

        deliveryRepository.save(delivery);

        OrderRequest orderRequest = new OrderRequest(delivery.getOrderId());

        orderClient.deliverOrder(orderRequest);
    }
    
    @Override
    public void failed(DeliveryRequest request) {
        Delivery delivery = getDeliveryById(request.getDeliveryId());

        delivery.setDeliveryState(DeliveryState.FAILED);

        deliveryRepository.save(delivery);

        OrderRequest orderRequest = new OrderRequest(delivery.getOrderId());

        orderClient.deliverOrderFailed(orderRequest);
    }

    private Delivery getDeliveryById(String deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Доставка не найдена, id = " + deliveryId));
    }
}