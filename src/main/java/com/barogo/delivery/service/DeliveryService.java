package com.barogo.delivery.service;

import com.barogo.common.exception.InvalidRequestException;
import com.barogo.common.exception.LoginFailedException;
import com.barogo.delivery.dto.DeliveryResponse;
import com.barogo.delivery.dto.DeliverySearchRequest;
import com.barogo.delivery.dto.UpdateAddressRequest;
import com.barogo.delivery.entity.Delivery;
import com.barogo.delivery.enums.DeliveryStatus;
import com.barogo.delivery.repository.DeliveryRepository;
import com.barogo.user.entity.User;
import com.barogo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final RegionValidationService regionValidationService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<DeliveryResponse> getDeliveries(String userId, DeliverySearchRequest req) {
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(req.getStartDate(), formatter);
            end = LocalDate.parse(req.getEndDate(), formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException("날짜 형식이 올바르지 않습니다. yyyy-MM-dd 형식을 사용하세요.");
        }

        if (start.isAfter(end)) {
            throw new InvalidRequestException("시작일은 종료일보다 이후일 수 없습니다.");
        }

        if (ChronoUnit.DAYS.between(start, end) > 3) {
            throw new InvalidRequestException("조회 가능한 최대 기간은 3일입니다.");
        }

        // 상태값 유효성 검증
        if (StringUtils.hasText(req.getStatus())) {
            try {
                DeliveryStatus.valueOf(req.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("존재하지 않는 배달 상태입니다: " + req.getStatus());
            }
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new LoginFailedException("존재하지 않는 사용자입니다."));

        Pageable pageable = PageRequest.of(
                req.getPage() - 1,
                req.getSize(),
                Sort.by(Sort.Direction.fromString(req.getSort()), "createdAt")
        );

        Page<Delivery> deliveries = deliveryRepository.searchDeliveries(user, req, pageable);

        if (deliveries.isEmpty()) {
            return Page.empty();
        }

        return deliveries.map(DeliveryResponse::fromEntity);
    }

    public void updateDeliveryAddress(Long deliveryId, UpdateAddressRequest request) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new InvalidRequestException("해당 배달 내역이 존재하지 않습니다."));

        if (delivery.isDeleted()) {
            throw new InvalidRequestException("삭제된 배달 건은 수정할 수 없습니다.");
        }

        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new InvalidRequestException("배달이 접수 상태일 때만 주소를 변경할 수 있습니다.");
        }

        // 지역 비교 (외부 API 활용)
        regionValidationService.validateAddressChange(delivery.getAddress(), request.getNewAddress());

        delivery.updateAddress(request.getNewAddress());
        deliveryRepository.save(delivery);
    }
}
