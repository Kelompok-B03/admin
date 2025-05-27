package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CampaignServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    private CampaignServiceImpl campaignService;
    private final String CAMPAIGN_SERVICE_URL = "http://localhost:8082";
    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        campaignService = new CampaignServiceImpl(restTemplate, CAMPAIGN_SERVICE_URL);
        
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
    void getTotalCampaigns_ShouldReturnCount() {
        // Arrange
        List<CampaignDTO> campaigns = Arrays.asList(
            createCampaignDTO("1", "Campaign 1", 10000, 5000, "SEDANG_BERLANGSUNG"),
            createCampaignDTO("2", "Campaign 2", 20000, 15000, "SELESAI")
        );

        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(campaigns, HttpStatus.OK));

        // Act
        Long result = campaignService.getTotalCampaigns();

        // Assert
        assertEquals(2L, result);
    }
    
    @Test
    void getActiveCampaigns_ShouldReturnActiveCount() {
        // Arrange
        List<CampaignDTO> activeCampaigns = Arrays.asList(
            createCampaignDTO("1", "Active Campaign", 10000, 5000, "SEDANG_BERLANGSUNG")
        );

        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign/status/SEDANG_BERLANGSUNG"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(activeCampaigns, HttpStatus.OK));

        // Act
        Long result = campaignService.getActiveCampaigns();

        // Assert
        assertEquals(1L, result);
    }

    @Test
    void getCompletedCampaigns_ShouldReturnCompletedCount() {
        // Arrange
        List<CampaignDTO> completedCampaigns = Arrays.asList(
            createCampaignDTO("2", "Completed Campaign", 20000, 20000, "SELESAI")
        );

        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign/status/SELESAI"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(completedCampaigns, HttpStatus.OK));

        // Act
        Long result = campaignService.getCompletedCampaigns();

        // Assert
        assertEquals(1L, result);
    }

    @Test
    void getActiveCampaignsList_ShouldReturnActiveCampaigns() {
        // Arrange
        List<CampaignDTO> activeCampaigns = Arrays.asList(
            createCampaignDTO("1", "Active Campaign 1", 10000, 5000, "SEDANG_BERLANGSUNG"),
            createCampaignDTO("2", "Active Campaign 2", 15000, 7500, "SEDANG_BERLANGSUNG")
        );

        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign/status/SEDANG_BERLANGSUNG"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(activeCampaigns, HttpStatus.OK));

        // Act
        List<CampaignDTO> result = campaignService.getActiveCampaignsList();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Active Campaign 1", result.get(0).getTitle());
        assertEquals("Active Campaign 2", result.get(1).getTitle());
    }

    @Test
    void getCompletedCampaignsList_ShouldReturnCompletedCampaigns() {
        // Arrange
        List<CampaignDTO> completedCampaigns = Arrays.asList(
            createCampaignDTO("3", "Completed Campaign 1", 20000, 20000, "SELESAI"),
            createCampaignDTO("4", "Completed Campaign 2", 30000, 30000, "SELESAI")
        );

        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign/status/SELESAI"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(completedCampaigns, HttpStatus.OK));

        // Act
        List<CampaignDTO> result = campaignService.getCompletedCampaignsList();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Completed Campaign 1", result.get(0).getTitle());
        assertEquals("Completed Campaign 2", result.get(1).getTitle());
    }

    @Test
    void getFundUsageProof_ShouldReturnProofs() {
        // Arrange
        UUID campaignId = UUID.randomUUID();
        CampaignDTO campaign = new CampaignDTO();
        campaign.setCampaignId(campaignId.toString());
        campaign.setTitle("Test Campaign");
        campaign.setUsageProofLink("proof.jpg");

        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign/" + campaignId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CampaignDTO.class)
        )).thenReturn(new ResponseEntity<>(campaign, HttpStatus.OK));

        // Act
        List<String> result = campaignService.getFundUsageProof(campaignId);

        // Assert
        assertEquals(1, result.size());
        assertEquals("proof.jpg", result.get(0));
    }

    @Test
    void getTotalCampaigns_WhenExceptionOccurs_ShouldReturnZero() {
        // Arrange
        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Connection error"));

        // Act
        Long result = campaignService.getTotalCampaigns();

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void getActiveCampaigns_WhenExceptionOccurs_ShouldReturnZero() {
        // Arrange
        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign/status/SEDANG_BERLANGSUNG"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Connection error"));

        // Act
        Long result = campaignService.getActiveCampaigns();

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void getFundUsageProof_WhenCampaignNotFound_ShouldReturnEmptyList() {
        // Arrange
        UUID campaignId = UUID.randomUUID();

        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign/" + campaignId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CampaignDTO.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        List<String> result = campaignService.getFundUsageProof(campaignId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getFundUsageProof_WhenNoProofLink_ShouldReturnEmptyList() {
        // Arrange
        UUID campaignId = UUID.randomUUID();
        CampaignDTO campaign = new CampaignDTO();
        campaign.setCampaignId(campaignId.toString());
        campaign.setTitle("Test Campaign");
        campaign.setUsageProofLink(null);

        when(restTemplate.exchange(
                eq(CAMPAIGN_SERVICE_URL + "/api/campaign/" + campaignId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CampaignDTO.class)
        )).thenReturn(new ResponseEntity<>(campaign, HttpStatus.OK));

        // Act
        List<String> result = campaignService.getFundUsageProof(campaignId);

        // Assert
        assertTrue(result.isEmpty());
    }

    private CampaignDTO createCampaignDTO(String id, String title, int targetAmount, int fundsCollected, String status) {
        CampaignDTO campaign = new CampaignDTO();
        campaign.setCampaignId(id);
        campaign.setTitle(title);
        campaign.setDescription("Test campaign description");
        campaign.setTargetAmount(targetAmount);
        campaign.setFundsCollected(fundsCollected);
        campaign.setStartDate(LocalDate.now().minusDays(10));
        campaign.setEndDate(LocalDate.now().plusDays(20));
        campaign.setStatus(status);
        campaign.setFundraiserId(UUID.randomUUID().toString());
        campaign.setUsageProofLink(status.equals("SELESAI") ? "proof.jpg" : null);
        return campaign;
    }
}