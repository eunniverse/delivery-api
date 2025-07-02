package com.barogo.delivery.repository;

import com.barogo.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 배달 조회 repository
 * @author ehjang
 */
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, DeliveryQueryRepository {
}
