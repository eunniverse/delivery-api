package com.barogo.common.util;

/**
 * 배달 지역 거리 계산
 * @author ehjang
 */
public class GeoUtil {
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (단위: km)
        double dLat = Math.toRadians(lat2 - lat1); // 위도 차
        double dLon = Math.toRadians(lon2 - lon1); // 경도 차

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 거리 (km)
    }

    public static boolean isSameRegion(String region1, String region2) {
        return region1 != null && region1.equals(region2);
    }
}

