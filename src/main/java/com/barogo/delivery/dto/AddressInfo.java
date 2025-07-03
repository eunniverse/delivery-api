package com.barogo.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class AddressInfo {
    private String region1;
    private double lat;
    private double lon;

    /**
     * 주소가 같은지 확인
     * @param other
     * @return
     */
    public boolean equalsAddress(AddressInfo other) {
        if (other == null) return false;

        return Objects.equals(this.region1, other.region1) &&
                Double.compare(this.lat, other.lat) == 0 &&
                Double.compare(this.lon, other.lon) == 0;
    }
}
