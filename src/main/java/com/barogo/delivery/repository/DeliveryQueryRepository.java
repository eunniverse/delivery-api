package com.barogo.delivery.repository;

import com.barogo.delivery.entity.Delivery;
import com.barogo.delivery.dto.DeliverySearchRequest;
import com.barogo.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 배달 동적 검색을 위한 QueryDSL 인터페이스
 * @author ehjang
 */
public interface DeliveryQueryRepository {
    Page<Delivery> searchDeliveries(User user, DeliverySearchRequest request, Pageable pageable);
}
