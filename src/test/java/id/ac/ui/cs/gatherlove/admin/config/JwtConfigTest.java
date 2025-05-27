package id.ac.ui.cs.gatherlove.admin.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtConfigTest {

    @Test
    void jwtDecoder_ShouldCreateJwtDecoder() {
        // Arrange
        JwtProperties jwtProperties = mock(JwtProperties.class);
        SecretKey mockKey = new SecretKeySpec(
            "abcdefghijklmnopqrstuvwxyz123456ABCDEFGHIJKLMNOPQRSTUVWXYZ789012".getBytes(), 
            "HmacSHA256"
        );
        when(jwtProperties.getKey()).thenReturn(mockKey);
        
        JwtConfig jwtConfig = new JwtConfig(jwtProperties);
        
        // Act & Assert
        JwtDecoder decoder = assertDoesNotThrow(() -> jwtConfig.jwtDecoder());
        assertNotNull(decoder);
    }
}