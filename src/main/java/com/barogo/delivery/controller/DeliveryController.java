package com.barogo.delivery.controller;

import com.barogo.common.util.JwtAuthenticationFilter;
import com.barogo.common.util.JwtUtil;
import com.barogo.delivery.dto.DeliveryResponse;
import com.barogo.delivery.dto.DeliverySearchRequest;
import com.barogo.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "배달 조회",
            description = "JWT 인증된 사용자의 배달 내역을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeliveryResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "요청 파라미터 오류"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "404", description = "조회 결과 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping
    public ResponseEntity<Page<DeliveryResponse>> getDeliveries(
            @Valid @ModelAttribute DeliverySearchRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(deliveryService.getDeliveries(userId, request));
    }
}
