package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.LoginRequestDTO;
import id.ac.ui.cs.gatherlove.admin.dto.LoginResponseDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;

    private AuthServiceImpl authService;
    private final String AUTH_SERVICE_URL = "http://localhost:8080";
    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(restTemplate, AUTH_SERVICE_URL);
        
        // Mock SecurityContextHolder static methods
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        
        // Default setup with authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn("mock-token");    }

    @AfterEach
    void tearDown() {
        if (securityContextHolderMock != null) {
            securityContextHolderMock.close();
        }
    }

    @Test
    void getTotalUsers_ShouldReturnCount() {
        // Arrange
        Map<String, Long> responseMap = new HashMap<>();
        responseMap.put("totalUsers", 42L);
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/api/users/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Long>>() {})
        )).thenReturn(new ResponseEntity<>(responseMap, HttpStatus.OK));

        // Act
        Long result = authService.getTotalUsers();

        // Assert
        assertEquals(42L, result);
    }

    @Test
    void getTotalUsers_WhenNoAuthentication_ShouldStillMakeRequest() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        
        Map<String, Long> responseMap = new HashMap<>();
        responseMap.put("totalUsers", 15L);
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/api/users/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Long>>() {})
        )).thenReturn(new ResponseEntity<>(responseMap, HttpStatus.OK));

        // Act
        Long result = authService.getTotalUsers();

        // Assert
        assertEquals(15L, result);
    }

    @Test
    void getTotalUsers_WhenResponseIsNull_ShouldReturnZero() {
        // Arrange
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/api/users/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Long>>() {})
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        Long result = authService.getTotalUsers();

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void blockUser_WhenNoAuthentication_ShouldThrowAccessDeniedException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String reason = "Test reason";
        
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authService.blockUser(userId, reason));
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        // Arrange
        List<UserDTO> expectedUsers = Arrays.asList(
                createUserDTO("user1@example.com", "User One"),
                createUserDTO("user2@example.com", "User Two")
        );
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/api/users"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<List<UserDTO>>() {})
        )).thenReturn(new ResponseEntity<>(expectedUsers, HttpStatus.OK));

        // Act
        List<UserDTO> result = authService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("user1@example.com", result.get(0).getEmail());
    }

    @Test
    void getAllUsers_WhenNoAuthentication_ShouldThrowIllegalStateException() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.getAllUsers()
        );
        assertEquals("No authentication token available", exception.getMessage());
    }

    @Test
    void getAllUsers_WhenNoCredentials_ShouldThrowIllegalStateException() {
        // Arrange
        when(authentication.getCredentials()).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> authService.getAllUsers());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserDTO expectedUser = createUserDTO("user@example.com", "Test User");
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/api/users/" + userId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UserDTO.class)
        )).thenReturn(new ResponseEntity<>(expectedUser, HttpStatus.OK));

        // Act
        UserDTO result = authService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        // Arrange
        String email = "test@example.com";
        UserDTO expectedUser = createUserDTO(email, "Test User");
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/api/users/email/" + email),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UserDTO.class)
        )).thenReturn(new ResponseEntity<>(expectedUser, HttpStatus.OK));

        // Act
        UserDTO result = authService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void login_ShouldReturnLoginResponse() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("password");
        
        LoginResponseDTO expectedResponse = new LoginResponseDTO();
        expectedResponse.setSuccess(true);
        expectedResponse.setToken("jwt-token");
        expectedResponse.setUser(createUserDTO("admin@example.com", "Admin User"));
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/auth/login"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(LoginResponseDTO.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        LoginResponseDTO result = authService.login(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("jwt-token", result.getToken());
    }

    @Test
    void isAdmin_WithAdminRole_ShouldReturnTrue() {
        // Arrange
        String token = "admin-token";
        Map<String, Object> userData = new HashMap<>();
        userData.put("roles", Arrays.asList("USER", "ADMIN"));
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/auth/me"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(new ResponseEntity<>(userData, HttpStatus.OK));

        // Act
        boolean result = authService.isAdmin(token);

        // Assert
        assertTrue(result);
    }

    @Test
    void isAdmin_WithoutAdminRole_ShouldReturnFalse() {
        // Arrange
        String token = "user-token";
        Map<String, Object> userData = new HashMap<>();
        userData.put("roles", Arrays.asList("USER"));
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/auth/me"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(new ResponseEntity<>(userData, HttpStatus.OK));

        // Act
        boolean result = authService.isAdmin(token);

        // Assert
        assertFalse(result);
    }

    @Test
    void isAdmin_WhenExceptionOccurs_ShouldReturnFalse() {
        // Arrange
        String token = "invalid-token";
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/auth/me"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenThrow(new RuntimeException("Service error"));

        // Act
        boolean result = authService.isAdmin(token);

        // Assert
        assertFalse(result);
    }


    @Test
    void getUserFromToken_ShouldReturnUser() {
        // Arrange
        String token = "valid-token";
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", UUID.randomUUID().toString());
        userData.put("email", "test@example.com");
        userData.put("name", "Test User");
        userData.put("active", true);
        userData.put("roles", Arrays.asList("USER", "ADMIN"));
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/auth/me"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(new ResponseEntity<>(userData, HttpStatus.OK));

        // Act
        UserDTO result = authService.getUserFromToken(token);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertTrue(result.isActive());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains("USER"));
        assertTrue(result.getRoles().contains("ADMIN"));
    }

    @Test
    void getUserFromToken_WhenResponseIsNull_ShouldReturnNull() {
        // Arrange
        String token = "token";
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/auth/me"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        UserDTO result = authService.getUserFromToken(token);

        // Assert
        assertNull(result);
    }

    @Test
    void getUserFromToken_WhenExceptionOccurs_ShouldReturnNull() {
        // Arrange
        String token = "invalid-token";
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/auth/me"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenThrow(new RuntimeException("Service error"));

        // Act
        UserDTO result = authService.getUserFromToken(token);

        // Assert
        assertNull(result);
    }

    @Test
    void getUserFromToken_WithInvalidUUID_ShouldHandleGracefully() {
        // Arrange
        String token = "valid-token";
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", "invalid-uuid");
        userData.put("email", "test@example.com");
        userData.put("name", "Test User");
        userData.put("roles", Arrays.asList("USER"));
        
        when(restTemplate.exchange(
                eq(AUTH_SERVICE_URL + "/auth/me"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(new ResponseEntity<>(userData, HttpStatus.OK));

        // Act
        UserDTO result = authService.getUserFromToken(token);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertNull(result.getId()); // Should be null due to invalid UUID
    }

    private UserDTO createUserDTO(String email, String name) {
        UserDTO user = new UserDTO();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setName(name);
        user.setActive(true);
        user.setRoles(Arrays.asList("USER"));
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}