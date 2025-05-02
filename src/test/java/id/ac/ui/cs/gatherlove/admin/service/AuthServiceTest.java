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
        LoginRequestDTO request = new LoginRequestDTO("admin1", "password");

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("ADMIN", response.getUser().getRole());
        assertNotNull(response.getToken());
    }

    @Test
    void loginFailedWithInvalidCredentials() {
        LoginRequestDTO request = new LoginRequestDTO("invalidUser", "wrongPassword");

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getUser());
        assertNull(response.getToken());
    }

    @Test
    void checkRoleAdmin() {
        String token = authService.login(new LoginRequestDTO("admin1", "password")).getToken();

        boolean isAdmin = authService.isAdmin(token);

        assertTrue(isAdmin);
    }

    @Test
    void checkRoleNonAdmin() {
        String token = authService.login(new LoginRequestDTO("fundraiser1", "password")).getToken();

        boolean isAdmin = authService.isAdmin(token);

        assertFalse(isAdmin);
    }
}