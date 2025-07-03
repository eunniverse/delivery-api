package com.barogo.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "주소 변경 요청 DTO")
public class UpdateAddressRequest {

    @NotBlank(message = "도착지 주소는 필수입니다.")
    @Schema(description = "변경할 도착지 주소", example = "서울시 강남구 역삼동 123-45")
    private String newAddress;
}