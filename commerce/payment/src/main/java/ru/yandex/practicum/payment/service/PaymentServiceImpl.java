package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.order.dto.OrderRequest;
import ru.yandex.practicum.api.payment.dto.PaymentDto;
import ru.yandex.practicum.api.payment.dto.PaymentRequest;
import ru.yandex.practicum.api.payment.enums.PaymentStatus;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.shopping.store.dto.ProductContentDto;
import ru.yandex.practicum.payment.client.order.OrderClient;
import ru.yandex.practicum.payment.client.shoppingstore.ShoppingStoreClient;
import ru.yandex.practicum.payment.dal.dao.PaymentRepository;
import ru.yandex.practicum.payment.dal.model.Payment;
import ru.yandex.practicum.payment.mapper.PaymentMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final ShoppingStoreClient shoppingStoreClient;

    private final OrderClient orderClient;

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        BigDecimal tax = getTax(orderDto.getTotalPrice());

        Payment payment = paymentMapper.toModel(orderDto, tax);
        payment.setStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);

        return paymentMapper.toDto(payment);
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        BigDecimal totalProductCost = BigDecimal.ZERO;
        
        for (Map.Entry<String, Integer> product : orderDto.getProducts().entrySet()) {
            ProductContentDto productInfo = shoppingStoreClient.getProductById(product.getKey());
            BigDecimal productPrice = productInfo.getPrice();
            
            BigDecimal productTotal = productPrice.multiply(BigDecimal.valueOf(product.getValue()));
            totalProductCost = totalProductCost.add(productTotal);
        }
        
        return totalProductCost;
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        BigDecimal tax = getTax(orderDto.getProductPrice());

        return orderDto.getProductPrice()
                .add(tax)
                .add(orderDto.getDeliveryPrice());
    }

    private static BigDecimal getTax(BigDecimal price) {
        return price
                .multiply(BigDecimal.TEN)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public void refund(PaymentRequest request) {
        Payment payment = getOrElseThrow(request.getPaymentId());

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        OrderRequest orderRequest = new OrderRequest(payment.getOrderId());
        orderClient.payOrder(orderRequest);
    }

    @Override
    public void failed(PaymentRequest request) {
        Payment payment = getOrElseThrow(request.getPaymentId());

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        OrderRequest orderRequest = new OrderRequest(payment.getOrderId());
        orderClient.payOrderFailed(orderRequest);
    }

    private Payment getOrElseThrow(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платеж с id = " + paymentId + " не найден"));
    }
}