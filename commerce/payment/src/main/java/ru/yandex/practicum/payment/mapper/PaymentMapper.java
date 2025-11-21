package ru.yandex.practicum.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.api.order.dto.OrderDto;
import ru.yandex.practicum.api.payment.dto.PaymentDto;
import ru.yandex.practicum.api.payment.enums.PaymentStatus;
import ru.yandex.practicum.payment.dal.model.Payment;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(source = "totalCost", target = "totalPayment")
    @Mapping(source = "deliveryCost", target = "deliveryTotal")
    @Mapping(source = "taxCost", target = "feeTotal")
    PaymentDto toDto(Payment payment);

    @Mapping(source = "orderDto.productPrice", target = "productCost")
    @Mapping(source = "orderDto.deliveryPrice", target = "deliveryCost")
    @Mapping(source = "orderDto.totalPrice", target = "totalCost")
    @Mapping(source = "taxPrice", target = "taxCost")
    Payment toModel(OrderDto orderDto, BigDecimal taxPrice);

    @Mapping(source = "status", target = "status")
    void updateStatus(PaymentStatus status, @MappingTarget Payment payment);

}
