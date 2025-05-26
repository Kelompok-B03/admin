package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@Slf4j
public class CampaignServiceImpl implements CampaignService {

    private final RestTemplate restTemplate;
    private final String campaignServiceUrl;

    public CampaignServiceImpl(
            RestTemplate restTemplate,
            @Value("${CAMPAIGN_DONATION_WALLET_SERVICE_URL}") String serviceUrl) {
        this.restTemplate = restTemplate;
        this.campaignServiceUrl = serviceUrl;
    }

    @Override
    public Long getTotalCampaigns() {
        try {
            ResponseEntity<List<CampaignDTO>> response = restTemplate.exchange(
                campaignServiceUrl + "/api/campaign",
                HttpMethod.GET,
                createRequestEntity(),
                new ParameterizedTypeReference<List<CampaignDTO>>() {}
            );
            
            List<CampaignDTO> campaigns = response.getBody();
            return campaigns != null ? (long) campaigns.size() : 0L;
        } catch (Exception e) {
            log.error("Error getting total campaigns: {}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Long getActiveCampaigns() {
        try {
            ResponseEntity<List<CampaignDTO>> response = restTemplate.exchange(
                campaignServiceUrl + "/api/campaign/status/SEDANG_BERLANGSUNG", 
                HttpMethod.GET,
                createRequestEntity(),
                new ParameterizedTypeReference<List<CampaignDTO>>() {}
            );
            
            List<CampaignDTO> campaigns = response.getBody();
            return campaigns != null ? (long) campaigns.size() : 0L;
        } catch (Exception e) {
            log.error("Error getting active campaigns count: {}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Long getCompletedCampaigns() {
        try {
            ResponseEntity<List<CampaignDTO>> response = restTemplate.exchange(
                campaignServiceUrl + "/api/campaign/status/SELESAI",
                HttpMethod.GET,
                createRequestEntity(),
                new ParameterizedTypeReference<List<CampaignDTO>>() {}
            );
            
            List<CampaignDTO> campaigns = response.getBody();
            return campaigns != null ? (long) campaigns.size() : 0L;
        } catch (Exception e) {
            log.error("Error getting completed campaigns count: {}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public List<CampaignDTO> getActiveCampaignsList() {
        try {
            ResponseEntity<List<CampaignDTO>> response = restTemplate.exchange(
                campaignServiceUrl + "/api/campaign/status/SEDANG_BERLANGSUNG", 
                HttpMethod.GET,
                createRequestEntity(),
                new ParameterizedTypeReference<List<CampaignDTO>>() {}
            );
            
            List<CampaignDTO> campaigns = response.getBody();
            return campaigns != null ? campaigns : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting active campaigns list: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<CampaignDTO> getCompletedCampaignsList() {
        try {
            ResponseEntity<List<CampaignDTO>> response = restTemplate.exchange(
                campaignServiceUrl + "/api/campaign/status/SELESAI",
                HttpMethod.GET,
                createRequestEntity(),
                new ParameterizedTypeReference<List<CampaignDTO>>() {}
            );
            
            List<CampaignDTO> campaigns = response.getBody();
            return campaigns != null ? campaigns : new ArrayList<>();
        } catch (HttpClientErrorException.Forbidden e) {
            log.error("Access denied (403) when accessing completed campaigns: {}", e.getMessage());
            // Tambahkan log untuk melihat token yang digunakan
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                log.debug("Current authentication: {}, authorities: {}", 
                    auth.getName(), auth.getAuthorities());
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting completed campaigns list: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getFundUsageProof(UUID campaignId) {
        try {
            // Dalam API sebenarnya, ini akan mengambil bukti penggunaan dana dari service
            // Untuk saat ini, hanya mengembalikan kampanye dan melihat proofOfFundUsage-nya
            ResponseEntity<CampaignDTO> response = restTemplate.exchange(
                campaignServiceUrl + "/api/campaign/" + campaignId,
                HttpMethod.GET,
                createRequestEntity(),
                CampaignDTO.class
            );
            
            CampaignDTO campaign = response.getBody();
            if (campaign != null && campaign.getProofOfFundUsage() != null) {
                return campaign.getProofOfFundUsage();
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting fund usage proof for campaign {}: {}", campaignId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    private HttpEntity<Void> createRequestEntity() {
        return new HttpEntity<>(getHeaders());
    }
    
    private HttpHeaders getHeaders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (auth != null && auth.getCredentials() != null) {
            String token = (String) auth.getCredentials();
            headers.setBearerAuth(token);
            log.debug("Adding bearer token to campaign service request");
        }
        
        return headers;
    }
}