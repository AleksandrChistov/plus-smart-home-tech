package ru.yandex.practicum.payment.dal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.api.payment.enums.PaymentStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @Column(name = "payment_id")
    @UuidGenerator
    private String paymentId;

    @Column(name = "product_cost")
    private BigDecimal productCost;

    @Column(name = "delivery_cost")
    private BigDecimal deliveryCost;

    @Column(name = "tax_cost")
    private BigDecimal taxCost;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

}
