package com.endava.internship;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ParkingLotApplicationIT {

    @Test
    void testContextLoader() {
        ParkingLotApplication.main(new String[]{});
    }
}