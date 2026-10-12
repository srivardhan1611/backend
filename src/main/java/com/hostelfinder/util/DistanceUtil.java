package com.hostelfinder.util;

public final class DistanceUtil {
    private static final double R = 6371.0;
    private DistanceUtil() { }

    /** Haversine distance in km, rounded to 2 decimals. */
    public static double km(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double d = 2 * R * Math.asin(Math.sqrt(a));
        return Math.round(d * 100.0) / 100.0;
    }
}
