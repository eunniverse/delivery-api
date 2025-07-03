package com.barogo.delivery.service;

import com.barogo.common.exception.InvalidRequestException;
import com.barogo.common.util.GeoUtil;
import com.barogo.delivery.dto.AddressInfo;
import com.barogo.delivery.external.AddressGeocodingClient;
import com.barogo.delivery.external.DirectionsApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionValidationService {

    private final AddressGeocodingClient addressGeocodingClient;
    private final DirectionsApiClient directionsApiClient;

    public void validateAddressChange(String oldAddress, String newAddress) {
        AddressInfo oldInfo = addressGeocodingClient.lookup(oldAddress);
        AddressInfo newInfo = addressGeocodingClient.lookup(newAddress);

        if (oldInfo.equalsAddress(newInfo)) {
            throw new InvalidRequestException("기존 주소와 동일한 주소로 변경할 수 없습니다.");
        }

        if (!GeoUtil.isSameRegion(oldInfo.getRegion1(), newInfo.getRegion1())) {
            throw new InvalidRequestException("주소 변경은 동일한 시/도 내에서만 가능합니다.");
        }

        double distance = GeoUtil.calculateDistance(
                oldInfo.getLat(), oldInfo.getLon(),
                newInfo.getLat(), newInfo.getLon()
        );

        if (distance > 20.0) {
            int duration = directionsApiClient.getEstimatedTravelTime(oldInfo, newInfo);
            if (duration > 30) {
                throw new InvalidRequestException("주소 변경은 30분 이내 도착 가능한 경우에만 가능합니다.");
            }
        }
    }
}
