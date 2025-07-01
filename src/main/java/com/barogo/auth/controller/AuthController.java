package com.barogo.auth.controller;

import com.barogo.auth.dto.LoginRequest;
import com.barogo.auth.dto.TokenRefreshRequest;
import com.barogo.auth.dto.TokenResponse;
import com.barogo.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인 controller
 * @author ehjang
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "ID, 비밀번호로 로그인하고 Access/Refresh Token을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "요청 파라미터 오류"),
                    @ApiResponse(responseCode = "401", description = "ID 또는 비밀번호 불일치"),
                    @ApiResponse(responseCode = "403", description = "휴면 계정"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/token/refresh")
    @Operation(
            summary = "AccessToken 재발급",
            description = "RefreshToken을 사용해 새로운 AccessToken 및 RefreshToken을 재발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "재발급 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "RefreshToken 누락 또는 형식 오류"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 RefreshToken"),
                    @ApiResponse(responseCode = "401", description = "사용자 정보 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "RefreshToken을 제거하여 로그아웃 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    public ResponseEntity<Void> logout(@RequestBody @Valid TokenRefreshRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
