package ru.yandex.practicum.payment.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.payment.dal.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
