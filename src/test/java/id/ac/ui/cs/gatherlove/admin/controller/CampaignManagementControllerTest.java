package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
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
        List<CampaignDTO> pendingCampaigns = Arrays.asList(
                new CampaignDTO(UUID.randomUUID(), "Kampanye Pending 1", "Deskripsi",
                        UUID.randomUUID(), "Fundraiser 1", new BigDecimal("1000000"),
                        BigDecimal.ZERO, LocalDate.now(), LocalDate.now().plusDays(30),
                        "PENDING", null, null)
        );

        List<CampaignDTO> activeCampaigns = Arrays.asList(
                new CampaignDTO(UUID.randomUUID(), "Kampanye Aktif 1", "Deskripsi",
                        UUID.randomUUID(), "Fundraiser 2", new BigDecimal("2000000"),
                        new BigDecimal("500000"), LocalDate.now().minusDays(5),
                        LocalDate.now().plusDays(25), "ACTIVE", null, null)
        );

        List<CampaignDTO> completedCampaigns = Arrays.asList(
                new CampaignDTO(UUID.randomUUID(), "Kampanye Selesai 1", "Deskripsi",
                        UUID.randomUUID(), "Fundraiser 3", new BigDecimal("3000000"),
                        new BigDecimal("2500000"), LocalDate.now().minusDays(40),
                        LocalDate.now().minusDays(10), "COMPLETED", null, null)
        );

        when(adminFacade.getPendingCampaigns()).thenReturn(pendingCampaigns);
        when(adminFacade.getActiveCampaigns()).thenReturn(activeCampaigns);
        when(adminFacade.getCompletedCampaigns()).thenReturn(completedCampaigns);

        // Act & Assert
        mockMvc.perform(get("/api/admin/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pendingCampaigns").exists())
                .andExpect(jsonPath("$.activeCampaigns").exists())
                .andExpect(jsonPath("$.completedCampaigns").exists());
    }

    @Test
    void getCampaignDetails_ShouldReturnCampaignDetailsPage() throws Exception {
        // Arrange
        CampaignDTO campaign = new CampaignDTO(
                campaignId, "Kampanye Pending", "Deskripsi",
                UUID.randomUUID(), "Fundraiser 1", new BigDecimal("1000000"),
                BigDecimal.ZERO, LocalDate.now(), LocalDate.now().plusDays(30),
                "PENDING", null, null
        );

        List<String> proofs = Arrays.asList("url1", "url2");

        when(adminFacade.getPendingCampaigns()).thenReturn(Arrays.asList(campaign));
        when(adminFacade.getActiveCampaigns()).thenReturn(Arrays.asList());
        when(adminFacade.getCompletedCampaigns()).thenReturn(Arrays.asList());
        when(adminFacade.getFundUsageProof(campaignId)).thenReturn(proofs);

        // Act & Assert
        mockMvc.perform(get("/api/admin/campaigns/" + campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaign").exists())
                .andExpect(jsonPath("$.proofs").exists());
    }

    @Test
    void verifyCampaign_Approve_ShouldReturnOk() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/admin/campaigns/" + campaignId + "/verify")
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(adminFacade, times(1)).verifyCampaign(campaignId, true, null);
    }

    @Test
    void verifyCampaign_Reject_ShouldReturnOk() throws Exception {
        // Act & Assert
        String rejectionReason = "Konten kampanye tidak sesuai kebijakan";

        mockMvc.perform(post("/api/admin/campaigns/" + campaignId + "/verify")
                        .param("approved", "false")
                        .param("rejectionReason", rejectionReason)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(adminFacade, times(1)).verifyCampaign(campaignId, false, rejectionReason);
    }
}