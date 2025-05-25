package id.ac.ui.cs.gatherlove.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpResponse;
import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Tambahkan interceptor untuk logging request/response
        restTemplate.setInterceptors(
            Collections.singletonList(
                (request, body, execution) -> {
                    // Log request
                    System.out.println("===== REQUEST =====");
                    System.out.println("URI: " + request.getURI());
                    System.out.println("Method: " + request.getMethod());
                    System.out.println("Headers: " + request.getHeaders());
                    
                    // Execute request
                    ClientHttpResponse response = execution.execute(request, body);
                    
                    // Log response (tidak memodifikasi respons asli)
                    System.out.println("===== RESPONSE =====");
                    System.out.println("Status: " + response.getStatusCode());
                    System.out.println("Headers: " + response.getHeaders());
                    
                    return response;
                }
            )
        );
        
        return restTemplate;
    }
}