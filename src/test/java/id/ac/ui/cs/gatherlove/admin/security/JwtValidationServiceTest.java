package id.ac.ui.cs.gatherlove.admin.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import id.ac.ui.cs.gatherlove.admin.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JwtValidationServiceTest {

    @Mock
    private JwtDecoder jwtDecoder;
    
    @Mock
    private JwtProperties jwtProperties;
    
    private JwtValidationService jwtValidationService;
    private SecretKey secretKey;
    private String testSecret = "abcdefghijklmnopqrstuvwxyz123456ABCDEFGHIJKLMNOPQRSTUVWXYZ789012";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        secretKey = new SecretKeySpec(testSecret.getBytes(), "HmacSHA256");
        when(jwtProperties.getIssuer()).thenReturn("test-issuer");
        when(jwtProperties.getKey()).thenReturn(secretKey);
        jwtValidationService = new JwtValidationService(jwtProperties, jwtDecoder);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() throws Exception {
        // Arrange
        String validToken = createValidJwtToken();
        
        // Act
        boolean result = jwtValidationService.validateToken(validToken);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() throws Exception {
        // Arrange
        String expiredToken = createExpiredJwtToken();
        
        // Act
        boolean result = jwtValidationService.validateToken(expiredToken);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void validateToken_WithInvalidIssuer_ShouldReturnFalse() throws Exception {
        // Arrange
        String tokenWithWrongIssuer = createJwtTokenWithIssuer("wrong-issuer");
        
        // Act
        boolean result = jwtValidationService.validateToken(tokenWithWrongIssuer);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void validateToken_WithInvalidSignature_ShouldReturnFalse() throws Exception {
        // Arrange
        String tokenWithInvalidSignature = createJwtTokenWithDifferentKey();
        
        // Act
        boolean result = jwtValidationService.validateToken(tokenWithInvalidSignature);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void validateToken_WhenNullToken_ShouldReturnFalse() {
        // Act
        boolean result = jwtValidationService.validateToken(null);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void validateToken_WhenEmptyToken_ShouldReturnFalse() {
        // Act
        boolean result = jwtValidationService.validateToken("");
        
        // Assert
        assertFalse(result);
    }

    @Test
    void validateToken_WithMalformedToken_ShouldReturnFalse() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";
        
        // Act
        boolean result = jwtValidationService.validateToken(malformedToken);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void parseJwt_ShouldReturnJwt() {
        // Arrange
        String validToken = "valid.jwt.token";
        Jwt expectedJwt = createTestJwt();
        when(jwtDecoder.decode(validToken)).thenReturn(expectedJwt);
        
        // Act
        Jwt result = jwtValidationService.parseJwt(validToken);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedJwt, result);
    }
    
    @Test
    void parseJwt_WhenExceptionThrown_ShouldThrowRuntimeException() {
        // Arrange
        String invalidToken = "invalid.token";
        when(jwtDecoder.decode(invalidToken)).thenThrow(new RuntimeException("Decode error"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> jwtValidationService.parseJwt(invalidToken));
        assertTrue(exception.getMessage().contains("Unable to parse JWT"));
    }

    private String createValidJwtToken() throws Exception {
        return createJwtToken("test-issuer", new Date(System.currentTimeMillis() + 300000)); // 5 minutes from now
    }

    private String createExpiredJwtToken() throws Exception {
        return createJwtToken("test-issuer", new Date(System.currentTimeMillis() - 60000)); // 1 minute ago
    }

    private String createJwtTokenWithIssuer(String issuer) throws Exception {
        return createJwtToken(issuer, new Date(System.currentTimeMillis() + 300000));
    }

    private String createJwtTokenWithDifferentKey() throws Exception {
        SecretKey differentKey = new SecretKeySpec("differentkey123456789012345678901234567890123456789012345".getBytes(), "HmacSHA256");
        
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("1234567890")
                .issuer("test-issuer")
                .expirationTime(new Date(System.currentTimeMillis() + 300000))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        JWSSigner signer = new MACSigner(differentKey);
        signedJWT.sign(signer);
        
        return signedJWT.serialize();
    }

    private String createJwtToken(String issuer, Date expirationTime) throws Exception {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("1234567890")
                .issuer(issuer)
                .expirationTime(expirationTime)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        JWSSigner signer = new MACSigner(secretKey);
        signedJWT.sign(signer);
        
        return signedJWT.serialize();
    }
    
    private Jwt createTestJwt() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "1234");
        
        return new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(300),
            headers,
            claims
        );
    }
}