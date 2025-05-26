package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CampaignManagementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminFacade adminFacade;

    @InjectMocks
    private CampaignManagementController campaignManagementController;

    private final UUID campaignId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(campaignManagementController).build();
    }

    @Test
    void getAllCampaigns_ShouldReturnCampaignsPage() throws Exception {
        // Arrange
        CampaignDTO activeCampaign = createCampaign(
            UUID.randomUUID().toString(), 
            "Kampanye Aktif 1",
            2000000,
            500000,
            LocalDate.now().minusDays(5),
            LocalDate.now().plusDays(25),
            "SEDANG_BERLANGSUNG"
        );
        
        CampaignDTO completedCampaign = createCampaign(
            UUID.randomUUID().toString(), 
            "Kampanye Selesai 1",
            3000000,
            3000000,
            LocalDate.now().minusDays(40),
            LocalDate.now().minusDays(10),
            "SELESAI"
        );

        List<CampaignDTO> activeCampaigns = Collections.singletonList(activeCampaign);
        List<CampaignDTO> completedCampaigns = Collections.singletonList(completedCampaign);

        when(adminFacade.getActiveCampaigns()).thenReturn(activeCampaigns);
        when(adminFacade.getCompletedCampaigns()).thenReturn(completedCampaigns);

        // Act & Assert
        mockMvc.perform(get("/api/admin/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCampaigns").exists())
                .andExpect(jsonPath("$.activeCampaigns[0].title").value("Kampanye Aktif 1"))
                .andExpect(jsonPath("$.completedCampaigns").exists())
                .andExpect(jsonPath("$.completedCampaigns[0].title").value("Kampanye Selesai 1"));
                
        // Verify
        verify(adminFacade, times(1)).getActiveCampaigns();
        verify(adminFacade, times(1)).getCompletedCampaigns();
    }

    @Test
    void getAllCampaigns_WhenNoActiveCampaigns_ShouldReturnEmptyList() throws Exception {
        // Arrange
        CampaignDTO completedCampaign = createCampaign(
            UUID.randomUUID().toString(), 
            "Kampanye Selesai 1",
            3000000,
            3000000,
            LocalDate.now().minusDays(40),
            LocalDate.now().minusDays(10),
            "SELESAI"
        );

        List<CampaignDTO> activeCampaigns = Collections.emptyList();
        List<CampaignDTO> completedCampaigns = Collections.singletonList(completedCampaign);

        when(adminFacade.getActiveCampaigns()).thenReturn(activeCampaigns);
        when(adminFacade.getCompletedCampaigns()).thenReturn(completedCampaigns);

        // Act & Assert
        mockMvc.perform(get("/api/admin/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCampaigns").isArray())
                .andExpect(jsonPath("$.activeCampaigns").isEmpty())
                .andExpect(jsonPath("$.completedCampaigns").isArray())
                .andExpect(jsonPath("$.completedCampaigns").isNotEmpty());
    }

    @Test
    void getCampaignDetails_ShouldReturnCampaignDetailsPage() throws Exception {
        // Arrange
        CampaignDTO campaign = createCampaign(
            campaignId.toString(),
            "Kampanye Aktif",
            1000000,
            500000,
            LocalDate.now(),
            LocalDate.now().plusDays(30),
            "SEDANG_BERLANGSUNG"
        );

        List<String> proofs = Arrays.asList("bukti1.jpg", "bukti2.jpg");

        when(adminFacade.getActiveCampaigns()).thenReturn(Collections.singletonList(campaign));
        when(adminFacade.getCompletedCampaigns()).thenReturn(Collections.emptyList());
        when(adminFacade.getFundUsageProof(campaignId)).thenReturn(proofs);

        // Act & Assert
        mockMvc.perform(get("/api/admin/campaigns/" + campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaign").exists())
                .andExpect(jsonPath("$.campaign.title").value("Kampanye Aktif"))
                .andExpect(jsonPath("$.proofs").isArray())
                .andExpect(jsonPath("$.proofs[0]").value("bukti1.jpg"))
                .andExpect(jsonPath("$.proofs[1]").value("bukti2.jpg"));
                
        // Verify
        verify(adminFacade, times(1)).getActiveCampaigns();
        verify(adminFacade, times(1)).getCompletedCampaigns();
        verify(adminFacade, times(1)).getFundUsageProof(campaignId);
    }

    @Test
    void getCampaignDetails_WhenCampaignNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        
        when(adminFacade.getActiveCampaigns()).thenReturn(Collections.emptyList());
        when(adminFacade.getCompletedCampaigns()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/admin/campaigns/" + nonExistentId))
                .andExpect(status().isNotFound());
                
        // Verify
        verify(adminFacade, times(1)).getActiveCampaigns();
        verify(adminFacade, times(1)).getCompletedCampaigns();
        verify(adminFacade, never()).getFundUsageProof(any(UUID.class));
    }

    @Test
    void getCampaignDetails_ShouldFindCompletedCampaign() throws Exception {
        // Arrange
        CampaignDTO campaign = createCampaign(
            campaignId.toString(),
            "Kampanye Selesai",
            1000000,
            1000000,
            LocalDate.now().minusDays(30),
            LocalDate.now().minusDays(5),
            "SELESAI"
        );

        List<String> proofs = Collections.singletonList("bukti_final.jpg");

        when(adminFacade.getActiveCampaigns()).thenReturn(Collections.emptyList());
        when(adminFacade.getCompletedCampaigns()).thenReturn(Collections.singletonList(campaign));
        when(adminFacade.getFundUsageProof(campaignId)).thenReturn(proofs);

        // Act & Assert
        mockMvc.perform(get("/api/admin/campaigns/" + campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaign").exists())
                .andExpect(jsonPath("$.campaign.title").value("Kampanye Selesai"))
                .andExpect(jsonPath("$.campaign.status").value("SELESAI"))
                .andExpect(jsonPath("$.proofs").isArray())
                .andExpect(jsonPath("$.proofs[0]").value("bukti_final.jpg"));
    }

    // Helper method untuk membuat objek CampaignDTO
    private CampaignDTO createCampaign(String id, String title, int target, int collected, 
                                      LocalDate start, LocalDate end, String status) {
        CampaignDTO campaign = new CampaignDTO();
        campaign.setCampaignId(id);
        campaign.setTitle(title);
        campaign.setDescription("Deskripsi kampanye");
        campaign.setFundraiserId(UUID.randomUUID().toString());
        campaign.setFundraiserName("Nama Fundraiser");
        campaign.setTargetAmount(target);
        campaign.setFundsCollected(collected);
        campaign.setStartDate(start);
        campaign.setEndDate(end);
        campaign.setStatus(status);
        return campaign;
    }
}