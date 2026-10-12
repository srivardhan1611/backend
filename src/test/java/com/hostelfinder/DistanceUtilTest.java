package com.hostelfinder;

import com.hostelfinder.service.HostelService;
import com.hostelfinder.util.DistanceUtil;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DistanceUtilTest {
    @Test
    void samePointIsZero() {
        assertEquals(0.0, DistanceUtil.km(17.44, 78.34, 17.44, 78.34));
    }

    @Test
    void oneDegreeLatitudeIsAbout111Km() {
        assertEquals(111.19, DistanceUtil.km(0, 0, 1, 0), 0.1);
    }

    @Test
    void facilityMatchingIgnoresCaseAndHyphen() {
        assertTrue(HostelService.hasFacility(List.of("Wi-Fi", "Laundry"), "wifi"));
        assertFalse(HostelService.hasFacility(List.of("Laundry"), "wifi"));
    }
}
