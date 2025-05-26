package id.ac.ui.cs.gatherlove.admin.service;

import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Primary 
@Slf4j
public class DonationServiceImpl implements DonationService {

    private final RestTemplate restTemplate;
    private final String donationServiceUrl;

    public DonationServiceImpl(
            RestTemplate restTemplate,
            @Value("${CAMPAIGN_DONATION_WALLET_SERVICE_URL}") String donationServiceUrl) {
        this.restTemplate = restTemplate;
        this.donationServiceUrl = donationServiceUrl;
    }

    @Override
    public Long getTotalDonations() {
        // Ambil token dari SecurityContext untuk autentikasi
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.debug("Getting donation count from external service");
        
        if (auth != null && auth.getCredentials() != null) {
            String token = (String) auth.getCredentials();
            headers.setBearerAuth(token);
            log.debug("Adding bearer token to donation service request");
        }
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        try {
            log.debug("Making request to {}", donationServiceUrl + "/api/donations/count");
            ResponseEntity<Long> response = restTemplate.exchange(
                    donationServiceUrl + "/api/donations/count",
                    HttpMethod.GET,
                    requestEntity,
                    Long.class
            );
            
            Long count = response.getBody();
            log.debug("Got donation count: {}", count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("Error getting donation count: {}", e.getMessage(), e);
            return 0L;  // Fallback ke 0 jika terjadi error
        }
    }

}