package id.ac.ui.cs.gatherlove.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DonationServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;

    private DonationServiceImpl donationService;
    private final String DONATION_SERVICE_URL = "http://localhost:8081";
    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        donationService = new DonationServiceImpl(restTemplate, DONATION_SERVICE_URL);
        
        // Mock SecurityContextHolder static methods
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        
        // Default setup with authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn("mock-token");
    }

    @AfterEach
    void tearDown() {
        if (securityContextHolderMock != null) {
            securityContextHolderMock.close();
        }
    }

    @Test
    void getTotalDonations_ShouldReturnCount() {
        // Arrange
        Long expectedCount = 42L;

        when(restTemplate.exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Long.class)
        )).thenReturn(new ResponseEntity<>(expectedCount, HttpStatus.OK));

        // Act
        Long result = donationService.getTotalDonations();

        // Assert
        assertEquals(expectedCount, result);
        verify(restTemplate).exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Long.class)
        );
    }

    @Test
    void getTotalDonations_WhenExceptionOccurs_ShouldReturnZero() {
        // Arrange
        when(restTemplate.exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Long.class)
        )).thenThrow(new RuntimeException("Connection error"));

        // Act
        Long result = donationService.getTotalDonations();

        // Assert
        assertEquals(0L, result);
    }
    
    @Test
    void getTotalDonations_WhenNoAuthentication_ShouldStillMakeRequest() {
        // Arrange
        Long expectedCount = 15L;
        
        // Override the default authentication setup for this test
        when(securityContext.getAuthentication()).thenReturn(null);
        
        when(restTemplate.exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Long.class)
        )).thenReturn(new ResponseEntity<>(expectedCount, HttpStatus.OK));

        // Act
        Long result = donationService.getTotalDonations();

        // Assert
        assertEquals(expectedCount, result);
        
        // Verify that the request was made without authentication headers
        verify(restTemplate).exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                argThat(entity -> {
                    HttpEntity<Void> httpEntity = (HttpEntity<Void>) entity;
                    HttpHeaders headers = httpEntity.getHeaders();
                    return headers.getContentType().equals(MediaType.APPLICATION_JSON) &&
                           !headers.containsKey("Authorization");
                }),
                eq(Long.class)
        );
    }

    @Test
    void getTotalDonations_WhenResponseIsNull_ShouldReturnZero() {
        // Arrange
        when(restTemplate.exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Long.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        Long result = donationService.getTotalDonations();

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void getTotalDonations_WhenAuthenticationHasNoCredentials_ShouldMakeRequestWithoutToken() {
        // Arrange
        Long expectedCount = 25L;
        
        // Setup authentication without credentials
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(null);
        
        when(restTemplate.exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Long.class)
        )).thenReturn(new ResponseEntity<>(expectedCount, HttpStatus.OK));

        // Act
        Long result = donationService.getTotalDonations();

        // Assert
        assertEquals(expectedCount, result);
        
        // Verify that the request was made without bearer token
        verify(restTemplate).exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                argThat(entity -> {
                    HttpEntity<Void> httpEntity = (HttpEntity<Void>) entity;
                    HttpHeaders headers = httpEntity.getHeaders();
                    return headers.getContentType().equals(MediaType.APPLICATION_JSON) &&
                           !headers.containsKey("Authorization");
                }),
                eq(Long.class)
        );
    }

    @Test
    void getTotalDonations_WithValidToken_ShouldIncludeAuthorizationHeader() {
        // Arrange
        Long expectedCount = 100L;
        String expectedToken = "valid-token-123";
        
        when(authentication.getCredentials()).thenReturn(expectedToken);
        
        when(restTemplate.exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Long.class)
        )).thenReturn(new ResponseEntity<>(expectedCount, HttpStatus.OK));

        // Act
        Long result = donationService.getTotalDonations();

        // Assert
        assertEquals(expectedCount, result);
        
        // Verify that the request was made with correct authorization header
        verify(restTemplate).exchange(
                eq(DONATION_SERVICE_URL + "/api/donations/count"),
                eq(HttpMethod.GET),
                argThat(entity -> {
                    HttpEntity<Void> httpEntity = (HttpEntity<Void>) entity;
                    HttpHeaders headers = httpEntity.getHeaders();
                    return headers.getContentType().equals(MediaType.APPLICATION_JSON) &&
                           headers.containsKey("Authorization") &&
                           headers.getFirst("Authorization").equals("Bearer " + expectedToken);
                }),
                eq(Long.class)
        );
    }
}