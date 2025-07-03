package com.barogo.delivery;

import com.barogo.common.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    // 샘플 사용자 ID (data.sql 기준)
    private final String userId = "test";

    private String createJwtToken() {
        return jwtUtil.createAccessToken(userId);
    }

    @DisplayName("배송 조회 성공")
    @Test
    public void getDeliveries_shouldReturnDataSuccessfully() throws Exception {
        String token = createJwtToken();

        mockMvc.perform(get("/api/deliveries")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", "2024-06-30")
                        .param("endDate", "2024-07-02")
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(greaterThanOrEqualTo(0)));
    }

    @DisplayName("배송 주소 변경 성공")
    @Test
    void changeAddress_shouldSuccessfully() throws Exception {
        // deliveryId=1이 상태가 PENDING이고 주소 변경 가능한 조건이라고 가정
        String token = createJwtToken();

        String payload = """
        {
            "newAddress": "서울특별시 마포구 월드컵북로 396"
        }
        """;

        mockMvc.perform(patch("/api/deliveries/1/address")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
    }

    @DisplayName("배송 주소 변경 실패 (완료상태)")
    @Test
    void changeAddress_shouldFailWithCompleteStatus() throws Exception {
        String token = createJwtToken();

        String payload = """
        {
            "newAddress": "서울특별시 강남구 역삼로 123"
        }
        """;

        mockMvc.perform(patch("/api/deliveries/3/address") // 상태가 COMPLETE인 배달이라고 가정
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("주소 변경 실패 - 토큰 없음")
    @Test
    void changeAddress_shouldFailWithoutToken() throws Exception {
        String payload = """
            {
                "newAddress": "서울특별시 마포구 월드컵북로 396"
            }
            """;

        mockMvc.perform(patch("/api/deliveries/1/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("주소 변경 실패 - 존재하지 않는 배송 ID")
    @Test
    void changeAddress_shouldFailWithNotFoundDeliveryId() throws Exception {
        String token = createJwtToken();

        String payload = """
    {
        "newAddress": "서울특별시 강남구 테헤란로 123"
    }
    """;

        mockMvc.perform(patch("/api/deliveries/999/address")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("배송 조회 실패 - 잘못된 날짜 포맷")
    @Test
    void getDeliveries_shouldFailWithInvalidDateFormat() throws Exception {
        String token = createJwtToken();

        mockMvc.perform(get("/api/deliveries")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", "20240630") // yyyy-MM-dd 아님
                        .param("endDate", "2024-07-02")
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "DESC"))
                .andExpect(status().isBadRequest());
    }
}
