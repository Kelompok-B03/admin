package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.service.DonationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/donations")
@CrossOrigin(origins = "*")
@Slf4j
public class DonationManagementController {

    private final DonationService donationService;

    @Autowired
    public DonationManagementController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getDonationCount() {
        try {
            log.info("Fetching donation count from external service");
            Long count = donationService.getTotalDonations();
            log.info("Received donation count: {}", count);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("status", "success");
            response.put("message", "Successfully connected to donation service");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error connecting to donation service: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("count", 0);
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to connect to donation service: " + e.getMessage());
            
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    @GetMapping("/connection-test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            log.info("Testing connection to donation service");
            Long count = donationService.getTotalDonations();
            
            Map<String, Object> response = new HashMap<>();
            response.put("connected", true);
            response.put("serviceUrl", "campaign-donation-wallet service");
            response.put("endpoint", "/api/donations/count");
            response.put("responseReceived", count != null);
            response.put("donationCount", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Connection test failed: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("connected", false);
            errorResponse.put("serviceUrl", "campaign-donation-wallet service");
            errorResponse.put("endpoint", "/api/donations/count");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.ok(errorResponse);
        }
    }
}