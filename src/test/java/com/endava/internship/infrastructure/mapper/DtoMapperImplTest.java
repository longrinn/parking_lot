package com.endava.internship.infrastructure.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class DtoMapperImplTest {

    private final DtoMapper dtoMapper = Mappers.getMapper(DtoMapper.class);

    @Test
    void testMap() {
        User user = User.builder()
                .id(1)
                .name("User Name")
                .phone("123456789")
                .role(new Role("User"))
                .build();

        UserUpdatedRoleResponse response = dtoMapper.map(user);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getRole().getName(), response.getRole().getName());
    }

    @Test
    void testMapWithNullInput() {
        UserUpdatedRoleResponse result = dtoMapper.map(null);

        assertNull(result);
    }

    @Test
    void testMapWithNullRole() {
        User user = User.builder()
                .id(1)
                .name("User Name")
                .phone("123456789")
                .role(null)
                .build();

        UserUpdatedRoleResponse response = dtoMapper.map(user);

        assertNull(response.getRole());
    }
}