package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import java.util.List;
import java.util.UUID;

public interface CampaignService {
    Long getTotalCampaigns();
    Long getActiveCampaigns();
    Long getCompletedCampaigns();
    List<CampaignDTO> getActiveCampaignsList();
    List<CampaignDTO> getCompletedCampaignsList();
    List<String> getFundUsageProof(UUID campaignId);
}