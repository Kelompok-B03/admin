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
        return null;
    }

    @GetMapping("/{campaignId}")
    public ResponseEntity<?> getCampaignDetails(@PathVariable UUID campaignId) {
        return null;
    }

    @PostMapping("/{campaignId}/verify")
    public ResponseEntity<Void> verifyCampaign(

        return null;
    }

    private CampaignDTO findCampaignById(UUID campaignId) {
        return null;
    }
}