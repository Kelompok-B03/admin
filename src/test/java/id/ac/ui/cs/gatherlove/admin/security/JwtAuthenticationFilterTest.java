package id.ac.ui.cs.gatherlove.admin.security;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtValidationService jwtValidationService;
    
    @Mock
    private AuthService authService;
    
    @Mock
    private FilterChain filterChain;
    
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtValidationService, authService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        
        // Clear SecurityContext before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAddAuthentication() throws Exception {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtValidationService.validateToken(validToken)).thenReturn(true);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID());
        userDTO.setEmail("user@example.com");
        userDTO.setName("Test User");
        userDTO.setRoles(Arrays.asList("USER", "ADMIN"));
        
        when(authService.getUserFromToken(validToken)).thenReturn(userDTO);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(validToken, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals("user@example.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        
        // Verify authorities are set correctly
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoAuthorizationHeader_ShouldNotAddAuthentication() throws Exception {
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtValidationService, never()).validateToken(anyString());
    }

    @Test
    void doFilterInternal_WithInvalidAuthorizationHeader_ShouldNotAddAuthentication() throws Exception {
        // Arrange
        request.addHeader("Authorization", "NotBearer token");
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtValidationService, never()).validateToken(anyString());
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotAddAuthentication() throws Exception {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + invalidToken);
        
        when(jwtValidationService.validateToken(invalidToken)).thenReturn(false);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(authService, never()).getUserFromToken(anyString());
    }

    @Test
    void doFilterInternal_WithValidTokenButNullUser_ShouldNotAddAuthentication() throws Exception {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtValidationService.validateToken(validToken)).thenReturn(true);
        when(authService.getUserFromToken(validToken)).thenReturn(null);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithEmptyRoles_ShouldUseFallbackRole() throws Exception {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtValidationService.validateToken(validToken)).thenReturn(true);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID());
        userDTO.setEmail("user@example.com");
        userDTO.setName("Test User");
        userDTO.setRoles(Collections.emptyList());
        
        when(authService.getUserFromToken(validToken)).thenReturn(userDTO);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNullRolesAndNullRole_ShouldUseDefaultUserRole() throws Exception {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtValidationService.validateToken(validToken)).thenReturn(true);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID());
        userDTO.setEmail("user@example.com");
        userDTO.setName("Test User");
        userDTO.setRoles(null);
        
        when(authService.getUserFromToken(validToken)).thenReturn(userDTO);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithRoleAlreadyHavingPrefix_ShouldNotAddDuplicatePrefix() throws Exception {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtValidationService.validateToken(validToken)).thenReturn(true);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID());
        userDTO.setEmail("user@example.com");
        userDTO.setName("Test User");
        userDTO.setRoles(Arrays.asList("ROLE_ADMIN", "USER")); // Mix of prefixed and non-prefixed
        
        when(authService.getUserFromToken(validToken)).thenReturn(userDTO);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenExceptionOccurs_ShouldContinueFilterChain() throws Exception {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtValidationService.validateToken(validToken)).thenReturn(true);
        when(authService.getUserFromToken(validToken)).thenThrow(new RuntimeException("Service error"));
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithEmptyBearerToken_ShouldNotAddAuthentication() throws Exception {
        // Arrange
        request.addHeader("Authorization", "Bearer ");
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtValidationService, never()).validateToken(anyString());
    }

    @Test
    void doFilterInternal_WithWhitespaceBearerToken_ShouldNotAddAuthentication() throws Exception {
        // Arrange
        request.addHeader("Authorization", "Bearer   ");
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtValidationService, never()).validateToken(anyString());
    }
}