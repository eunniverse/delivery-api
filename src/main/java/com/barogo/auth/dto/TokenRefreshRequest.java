package com.barogo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Refresh Token을 사용하여 Access Token을 재발급 받기 위한 요청 DTO")
public class TokenRefreshRequest {
    @Schema(description = "재발급에 사용되는 Refresh Token", example = "cf63b9c0-d927-47c1-9a6b-1234abcd")
    @NotBlank
    private String refreshToken;
}
