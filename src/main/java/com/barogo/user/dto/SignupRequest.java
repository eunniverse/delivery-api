package com.barogo.user.dto;

import com.barogo.user.validation.UniqueUserId;
import com.barogo.user.validation.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignupRequest {

    @Schema(description = "사용자 ID", example = "eunisss98")
    @NotBlank
    @UniqueUserId
    private String userId;

    @Schema(description = "비밀번호 (12자 이상, 영문+숫자+특수문자 포함)", minLength = 12, example = "Password12345!!")
    @NotBlank
    @ValidPassword
    private String password;

    @Schema(description = "사용자 이름", example = "장은희")
    @NotBlank
    private String name;
}

