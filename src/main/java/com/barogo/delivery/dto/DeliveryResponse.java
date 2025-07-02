package com.barogo.delivery.dto;

import com.barogo.delivery.entity.Delivery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Builder
@Getter
@Schema(description = "배달 응답 DTO")
public class DeliveryResponse {

    @Schema(description = "배달 ID", example = "1")
    private Long id;

    @Schema(description = "배송지 주소", example = "서울시 강남구")
    private String address;

    @Schema(description = "배달 상태", example = "COMPLETE")
    private String status;

    @Schema(description = "생성 일시", example = "2024-07-01T12:00:00")
    private LocalDateTime createdAt;

    public static DeliveryResponse fromEntity(Delivery d) {
        return DeliveryResponse.builder()
                .id(d.getId())
                .address(d.getAddress())
                .status(d.getStatus().name())
                .createdAt(d.getCreatedAt())
                .build();
    }
}