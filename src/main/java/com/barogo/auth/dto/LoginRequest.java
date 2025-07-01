package com.barogo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

    @Schema(description = "사용자 ID", example = "eunisss98")
    @NotBlank(message = "입력값을 확인해주세요.")
    private String userId;

    @Schema(description = "비밀번호", example = "Password12345!!")
    @NotBlank(message = "입력값을 확인해주세요.")
    private String password;
}