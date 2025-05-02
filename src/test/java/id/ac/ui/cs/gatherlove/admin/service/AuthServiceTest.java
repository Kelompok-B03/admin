package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.LoginRequestDTO;
import id.ac.ui.cs.gatherlove.admin.dto.LoginResponseDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new DummyAuthServiceImpl();
    }

    @Test
    void loginSuccess() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO("admin1", "password");

        // When
        LoginResponseDTO response = authService.login(request);

        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("ADMIN", response.getUser().getRole());
        assertNotNull(response.getToken());
    }

    @Test
    void loginFailedWithInvalidCredentials() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO("invalidUser", "wrongPassword");

        // When
        LoginResponseDTO response = authService.login(request);

        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getUser());
        assertNull(response.getToken());
    }

    @Test
    void checkRoleAdmin() {
        // Given
        String token = authService.login(new LoginRequestDTO("admin1", "password")).getToken();

        // When
        boolean isAdmin = authService.isAdmin(token);

        // Then
        assertTrue(isAdmin);
    }

    @Test
    void checkRoleNonAdmin() {
        // Given
        String token = authService.login(new LoginRequestDTO("fundraiser1", "password")).getToken();

        // When
        boolean isAdmin = authService.isAdmin(token);

        // Then
        assertFalse(isAdmin);
    }
}