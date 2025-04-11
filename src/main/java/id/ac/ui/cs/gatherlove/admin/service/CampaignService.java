package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import java.util.List;
import java.util.UUID;

public interface CampaignService {
    Long getTotalCampaigns();
    Long getPendingCampaigns();
    Long getActiveCampaigns();
    Long getCompletedCampaigns();
    List<CampaignDTO> getPendingCampaignsList();
    List<CampaignDTO> getActiveCampaignsList();
    List<CampaignDTO> getCompletedCampaignsList();
    void verifyCampaign(UUID campaignId, boolean approved, String rejectionReason);
    List<String> getFundUsageProof(UUID campaignId);
}