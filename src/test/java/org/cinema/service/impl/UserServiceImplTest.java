package org.cinema.service.impl;

import org.cinema.config.JpaConfigTest;
import org.cinema.dto.userDTO.UserCreateDTO;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.mapper.userMapper.UserCreateMapper;
import org.cinema.model.User;
import org.cinema.repository.UserRepository;
import org.cinema.service.UserService;
import org.cinema.util.PasswordUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {JpaConfigTest.class, UserServiceImpl.class, UserRepository.class})
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static UserCreateDTO userCreateDTO;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userCreateDTO = UserCreateDTO.builder()
                .username("testUser")
                .password("securePassword")
                .role("ROLE_USER")
                .build();

        User testUser = UserCreateMapper.INSTANCE.toEntity(userCreateDTO);
        testUser.setPassword(PasswordUtil.hashPassword(userCreateDTO.getPassword()));

        userRepository.save(testUser);
    }

    @Test
    void testSaveUser() {
        userRepository.deleteAll();
        String response = userService.save(userCreateDTO);

        User savedUser = userRepository.findByUsername("testUser").orElse(null);
        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
        assertTrue(response.contains("successfully added!"));
    }

    @Test
    void testFindById() {
        User savedUser = userRepository.findByUsername("testUser").orElse(null);
        assertNotNull(savedUser);

        UserResponseDTO responseDTO = userService.getById(savedUser.getId().toString());

        assertNotNull(responseDTO);
        assertEquals(savedUser.getUsername(), responseDTO.getUsername());
    }

    @Test
    void testFindByIdNotFound() {
        NoDataFoundException exception = assertThrows(NoDataFoundException.class, () -> userService.getById("9999"));
        assertTrue(exception.getMessage().contains("doesn't exist"));
    }

    @Test
    void testDeleteUser() {
        User savedUser = userRepository.findByUsername("testUser").orElse(null);
        assertNotNull(savedUser);

        String response = userService.delete(savedUser.getId().toString());

        User deletedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNull(deletedUser);
        assertTrue(response.contains("successfully deleted!"));
    }
}
