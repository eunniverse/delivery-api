package com.barogo.delivery.external;

import com.barogo.common.exception.InvalidRequestException;
import com.barogo.delivery.dto.AddressInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;
import org.json.JSONArray;
import java.net.URI;

/**
 * 예측 이동 시간 계산 (외부 API 활용)
 * @author ehjang
 */
@Component
@Slf4j
public class DirectionsApiClient {

    private final WebClient webClient = WebClient.create();

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public int getEstimatedTravelTime(AddressInfo from, AddressInfo to) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis-navi.kakaomobility.com/v1/directions")
                .queryParam("origin", from.getLon() + "," + from.getLat())
                .queryParam("destination", to.getLon() + "," + to.getLat())
                .build()
                .encode()
                .toUri();

        String response = webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONObject json = new JSONObject(response);

        // 1. routes 존재 여부 및 배열인지 확인
        JSONArray routes = json.optJSONArray("routes");
        if (routes == null || routes.isEmpty()) {
            log.warn("routes 비어있음");
            throw new InvalidRequestException("경로 정보를 가져올 수 없습니다.");
        }

        // 2. 첫 번째 route 객체 가져오기
        JSONObject route = routes.optJSONObject(0);
        if (route == null) {
            log.warn("routes[0] 없음");
            throw new InvalidRequestException("경로 정보가 잘못되었습니다.");
        }

        // 3. summary 객체 확인
        JSONObject summary = route.optJSONObject("summary");
        if (summary == null || !summary.has("duration")) {
            log.warn("요약 정보(summary)가 없거나 duration 필드가 없음, summary = {}", summary);
            throw new InvalidRequestException("경로 정보를 가져올 수 없습니다. ");
        }

        // 4. duration 추출 및 타입 안전성 확인
        int durationInSeconds;
        try {
            durationInSeconds = summary.getInt("duration");
        } catch (JSONException e) {
            log.warn("duration 필드 파싱 실패, summary = {}", summary);
            throw new InvalidRequestException("경로 시간 정보가 유효하지 않습니다.");
        }

        // 5. 분 단위로 변환 후 반환
        return durationInSeconds / 60;
    }
}
