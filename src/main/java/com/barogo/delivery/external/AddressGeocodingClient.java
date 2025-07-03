package com.barogo.delivery.external;

import com.barogo.common.exception.InvalidRequestException;
import com.barogo.delivery.dto.AddressInfo;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;
import java.net.URI;

/**
 * 주소로 위도,경도 확인 (외부 API 활용)
 * @author ehjang
 */
@Component
@RequiredArgsConstructor
public class AddressGeocodingClient {

    private final WebClient webClient;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public AddressInfo lookup(String address) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", address)
                .build()
                .encode()
                .toUri();

        String response = webClient.get()
                .uri(uri)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONObject json = new JSONObject(response);

        // 1. documents 존재 여부 및 비어있는지 확인
        JSONArray documents = json.optJSONArray("documents");
        if (documents == null || documents.isEmpty()) {
            throw new InvalidRequestException("주소를 찾을 수 없습니다: " + address);
        }

        // 2. documents[0] 가져오기
        JSONObject doc = documents.optJSONObject(0);
        if (doc == null || !doc.has("address")) {
            throw new InvalidRequestException("주소 정보가 잘못되었습니다: " + address);
        }

        // 3. address 내부 구조 확인
        JSONObject addr = doc.optJSONObject("address");
        if (addr == null ||
                !addr.has("region_1depth_name") ||
                !addr.has("x") ||
                !addr.has("y")) {
            throw new InvalidRequestException("주소의 상세 정보가 부족합니다: " + address);
        }

        // 4. 실제 데이터 추출
        String region1 = addr.getString("region_1depth_name");
        double lat = addr.getDouble("y");
        double lon = addr.getDouble("x");

        return new AddressInfo(region1, lat, lon);
    }
}
