package id.ac.ui.cs.gatherlove.admin.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateConfigTest {

    private RestTemplateConfig restTemplateConfig = new RestTemplateConfig();

    @Test
    void restTemplate_ShouldReturnRestTemplateWithInterceptor() {
        // Act
        RestTemplate restTemplate = restTemplateConfig.restTemplate();
        
        // Assert
        assertNotNull(restTemplate);
        assertEquals(1, restTemplate.getInterceptors().size());
        assertTrue(restTemplate.getInterceptors().get(0) instanceof ClientHttpRequestInterceptor);
    }
}