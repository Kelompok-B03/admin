package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DonationManagementControllerTest {

    @Mock
    private DonationService donationService;
    
    @InjectMocks
    private DonationManagementController controller;
    
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getDonationCount_ShouldReturnCount() {
        // Arrange
        when(donationService.getTotalDonations()).thenReturn(42L);
        
        // Act
        ResponseEntity<Map<String, Object>> response = controller.getDonationCount();
        
        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(42L, response.getBody().get("count"));
        assertEquals("success", response.getBody().get("status"));
    }

    @Test
    void getDonationCount_WhenExceptionOccurs_ShouldReturnError() {
        // Arrange
        when(donationService.getTotalDonations()).thenThrow(new RuntimeException("Connection error"));
        
        // Act
        ResponseEntity<Map<String, Object>> response = controller.getDonationCount();
        
        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("error", response.getBody().get("status"));
        assertTrue(response.getBody().get("message").toString().contains("Connection error"));
    }
    
    @Test
    void testConnection_ShouldReturnConnectionStatus() {
        // Arrange
        when(donationService.getTotalDonations()).thenReturn(10L);
        
        // Act
        ResponseEntity<Map<String, Object>> response = controller.testConnection();
        
        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody().get("connected"));
        assertEquals(10L, response.getBody().get("donationCount"));
    }
    
    @Test
    void testConnection_WhenExceptionOccurs_ShouldReturnDisconnected() {
        // Arrange
        when(donationService.getTotalDonations()).thenThrow(new RuntimeException("Connection error"));
        
        // Act
        ResponseEntity<Map<String, Object>> response = controller.testConnection();
        
        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertFalse((Boolean) response.getBody().get("connected"));
        assertTrue(response.getBody().get("error").toString().contains("Connection error"));
    }
}