package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/campaigns")
@CrossOrigin(origins = "*") // Sesuaikan dengan kebutuhan keamanan
public class CampaignManagementController {

    private final AdminFacade adminFacade;

    @Autowired
    public CampaignManagementController(AdminFacade adminFacade) {
        this.adminFacade = adminFacade;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<CampaignDTO>>> getAllCampaigns() {
        Map<String, List<CampaignDTO>> response = new HashMap<>();
        response.put("activeCampaigns", adminFacade.getActiveCampaigns());
        response.put("completedCampaigns", adminFacade.getCompletedCampaigns());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{campaignId}")
    public ResponseEntity<?> getCampaignDetails(@PathVariable UUID campaignId) {
        CampaignDTO campaign = findCampaignById(campaignId);

        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("campaign", campaign);
        response.put("proofs", adminFacade.getFundUsageProof(campaignId));

        return ResponseEntity.ok(response);
    }

    private CampaignDTO findCampaignById(UUID campaignId) {
        List<CampaignDTO> allCampaigns = adminFacade.getActiveCampaigns();
        allCampaigns.addAll(adminFacade.getCompletedCampaigns());

        return allCampaigns.stream()
                .filter(campaign -> campaignId.equals(UUID.fromString(campaign.getCampaignId())))
                .findFirst()
                .orElse(null);
    }
}