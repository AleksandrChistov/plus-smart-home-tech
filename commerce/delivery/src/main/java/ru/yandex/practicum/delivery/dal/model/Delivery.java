package ru.yandex.practicum.delivery.dal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.api.delivery.enums.DeliveryState;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Delivery {
    @Id
    @Column(name = "delivery_id")
    @UuidGenerator
    private String deliveryId;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false)
    private DeliveryState deliveryState = DeliveryState.CREATED;

    @Column(name = "from_country")
    private String fromCountry;

    @Column(name = "from_city")
    private String fromCity;

    @Column(name = "from_street")
    private String fromStreet;

    @Column(name = "from_house")
    private String fromHouse;

    @Column(name = "from_flat")
    private String fromFlat;

    @Column(name = "to_country")
    private String toCountry;

    @Column(name = "to_city")
    private String toCity;

    @Column(name = "to_street")
    private String toStreet;

    @Column(name = "to_house")
    private String toHouse;

    @Column(name = "to_flat")
    private String toFlat;
}