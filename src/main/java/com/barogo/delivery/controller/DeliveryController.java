package com.barogo.delivery.controller;

import com.barogo.common.util.JwtUtil;
import com.barogo.delivery.dto.DeliveryResponse;
import com.barogo.delivery.dto.DeliverySearchRequest;
import com.barogo.delivery.dto.UpdateAddressRequest;
import com.barogo.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(
            summary = "배달 도착지 주소 변경",
            description = "사용자의 배달 도착지 주소를 변경합니다. (접수 상태(PENDING)만 가능, 같은 시/도 내에서만 허용)",
            responses = {
                @ApiResponse(responseCode = "200", description = "주소 변경 성공"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 변경 불가 상태"),
                @ApiResponse(responseCode = "404", description = "해당 배달 내역 없음")
            }
    )
    @PatchMapping("/{deliveryId}/address")
    public ResponseEntity<Void> updateAddress(
            @PathVariable Long deliveryId,
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        deliveryService.updateDeliveryAddress(deliveryId, request);
        return ResponseEntity.ok().build();
    }
}
