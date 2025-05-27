package id.ac.ui.cs.gatherlove.admin.config;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class JwtPropertiesTest {

    @Test
    void jwtProperties_ShouldHaveProperGettersAndSetters() {
        // Arrange
        JwtProperties jwtProperties = new JwtProperties();
        // Use exactly 32 characters (256 bits) for HS256
        String secretKeyString = "abcdefghijklmnopqrstuvwxyz123456";
        String issuer = "test-issuer";
        String algorithm = "HS256";
        
        // Act
        jwtProperties.setKey(secretKeyString);
        jwtProperties.setIssuer(issuer);
        jwtProperties.setAlgorithm(algorithm);
        
        // Assert
        // Note: getKey() returns SecretKey object, not the original string
        assertNotNull(jwtProperties.getKey());
        assertEquals("HmacSHA256", jwtProperties.getKey().getAlgorithm());
        assertEquals(issuer, jwtProperties.getIssuer());
        assertEquals(algorithm, jwtProperties.getAlgorithm());
    }
    
    @Test
    void getKey_ShouldReturnValidKey() {
        // Arrange
        JwtProperties jwtProperties = new JwtProperties();
        // Use exactly 32 characters (256 bits) for HS256
        jwtProperties.setKey("abcdefghijklmnopqrstuvwxyz123456");
        jwtProperties.setAlgorithm("HS256");
        
        // Act
        SecretKey key = jwtProperties.getKey();
        
        // Assert
        assertNotNull(key);
        assertEquals("HmacSHA256", key.getAlgorithm());
    }
}