package com.endava.internship.infrastructure.util;

import org.junit.jupiter.api.Test;

import static com.endava.internship.infrastructure.util.ParkingLotConstants.CREDENTIALS_NOT_FOUND_ERROR_MESSAGE;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.ROLE_NOT_FOUND_ERROR_MESSAGE;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.USER_NOT_FOUND_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ParkingLotConstantsTest {

    @Test
    void testParkingLotConstantsClass() {
        ParkingLotConstants parkingLotConstants = new ParkingLotConstants();
        assertNotNull(parkingLotConstants);
        assertNotNull(USER_NOT_FOUND_ERROR_MESSAGE);
        assertNotNull(ROLE_NOT_FOUND_ERROR_MESSAGE);
        assertNotNull(CREDENTIALS_NOT_FOUND_ERROR_MESSAGE);
    }
}