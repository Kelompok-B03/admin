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

        when(adminFacade.getPendingCampaigns()).thenReturn(pendingCampaigns);
        when(adminFacade.getActiveCampaigns()).thenReturn(activeCampaigns);

        // Act & Assert
        mockMvc.perform(get("/admin/campaigns"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/campaigns"))
                .andExpect(model().attributeExists("pendingCampaigns"))
                .andExpect(model().attributeExists("activeCampaigns"));
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
        when(adminFacade.getFundUsageProof(campaignId)).thenReturn(proofs);

        // Act & Assert
        mockMvc.perform(get("/admin/campaigns/" + campaignId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/campaign-details"))
                .andExpect(model().attributeExists("campaign"))
                .andExpect(model().attributeExists("proofs"));
    }

    @Test
    void verifyCampaign_Approve_ShouldRedirectToCampaigns() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/campaigns/" + campaignId + "/verify")
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/campaigns"));

        verify(adminFacade, times(1)).verifyCampaign(campaignId, true, null);
    }

    @Test
    void verifyCampaign_Reject_ShouldRedirectToCampaigns() throws Exception {
        // Act & Assert
        String rejectionReason = "Konten kampanye tidak sesuai kebijakan";

        mockMvc.perform(post("/admin/campaigns/" + campaignId + "/verify")
                        .param("approved", "false")
                        .param("rejectionReason", rejectionReason)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/campaigns"));

        verify(adminFacade, times(1)).verifyCampaign(campaignId, false, rejectionReason);
    }
}