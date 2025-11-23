package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.order.dto.OrderRequest;
import ru.yandex.practicum.api.order.dto.ProductReturnRequest;
import ru.yandex.practicum.api.order.enums.OrderState;
import ru.yandex.practicum.api.payment.dto.PaymentDto;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.api.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.order.client.delivery.DeliveryClient;
import ru.yandex.practicum.order.client.payment.PaymentClient;
import ru.yandex.practicum.order.client.warehouse.WarehouseClient;
import ru.yandex.practicum.order.dal.dao.OrderDeliveryAddressRepository;
import ru.yandex.practicum.order.dal.dao.OrderRepository;
import ru.yandex.practicum.order.dal.model.Order;
import ru.yandex.practicum.order.dal.model.OrderDeliveryAddress;
import ru.yandex.practicum.order.mapper.OrderMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final DeliveryClient deliveryClient;

    private final WarehouseClient warehouseClient;

    private final PaymentClient paymentClient;

    private final OrderRepository orderRepository;

    private final OrderDeliveryAddressRepository orderDeliveryAddressRepository;

    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getOrdersByUsername(String username) {
        return orderMapper.toDtoList(orderRepository.findByUsername(username));
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {

        ShoppingCartDto shoppingCart = request.getShoppingCart();

        Order order = Order.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(shoppingCart.getProducts())
                .build();

        orderRepository.save(order);

        AddressDto address = request.getDeliveryAddress();

        OrderDeliveryAddress deliveryAddress = OrderDeliveryAddress.builder()
                .orderId(order.getOrderId())
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();

        orderDeliveryAddressRepository.save(deliveryAddress);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto calculateDelivery(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        AddressDto toAddress = getToAddressFromDb(order.getOrderId());

        AddressDto fromAddress = warehouseClient.getWarehouseAddress();

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(order.getOrderId())
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .build();

        DeliveryDto createdDelivery = deliveryClient.create(deliveryDto);

        order.setDeliveryId(createdDelivery.getDeliveryId());

        OrderDto orderDto = orderMapper.toDto(order);

        BigDecimal deliveryCost = deliveryClient.calculateTotal(orderDto);

        order.setDeliveryPrice(deliveryCost);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    private AddressDto getToAddressFromDb(String orderId) {
        Optional<OrderDeliveryAddress> deliveryAddressOpt = orderDeliveryAddressRepository.findByOrderId(orderId);

        if (deliveryAddressOpt.isEmpty()) {
            return null;
        }

        OrderDeliveryAddress deliveryAddress = deliveryAddressOpt.get();

        return AddressDto.builder()
                .country(deliveryAddress.getCountry())
                .city(deliveryAddress.getCity())
                .street(deliveryAddress.getStreet())
                .house(deliveryAddress.getHouse())
                .flat(deliveryAddress.getFlat())
                .build();
    }

    @Override
    public OrderDto calculateTotal(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        BigDecimal productCost = paymentClient.calculateProductCost(orderMapper.toDto(order));

        order.setProductPrice(productCost);

        BigDecimal totalCost = paymentClient.calculateTotalCost(orderMapper.toDto(order));

        order.setTotalPrice(totalCost);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto assembleOrder(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        AssemblyProductsForOrderRequest assemblyRequest = new AssemblyProductsForOrderRequest(order.getOrderId(), order.getProducts());

        BookedProductsDto bookedProductsDto = warehouseClient.assemblyProducts(assemblyRequest);

        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setFragile(bookedProductsDto.getFragile());
        order.setState(OrderState.ASSEMBLED);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto assembleOrderFailed(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        order.setDeliveryWeight(0.0f);
        order.setDeliveryVolume(0.0f);
        order.setFragile(null);
        order.setState(OrderState.ASSEMBLY_FAILED);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto payOrder(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        OrderDto orderDto = orderMapper.toDto(order);

        PaymentDto paymentDto = paymentClient.payment(orderDto);

        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.PAID);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto payOrderFailed(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        order.setState(OrderState.PAYMENT_FAILED);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto deliverOrder(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        order.setState(OrderState.DELIVERED);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto deliverOrderFailed(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        order.setState(OrderState.DELIVERY_FAILED);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto completeOrder(OrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        order.setState(OrderState.COMPLETED);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Заказ не найден, id = " + request.getOrderId()));

        Map<String, Integer> returnedProducts = request.getProducts();

        order.getProducts().forEach((productId, quantity) -> {
            if (returnedProducts.containsKey(productId)) {
                int returnedQuantity = returnedProducts.get(productId);
                int newQuantity = quantity - returnedQuantity;
                if (newQuantity <= 0) {
                    order.getProducts().remove(productId);
                } else {
                    order.getProducts().put(productId, newQuantity);
                }
            }
        });

        if (order.getProducts().isEmpty()) {
            order.setState(OrderState.PRODUCT_RETURNED);
        }

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }
}
