package id.ac.ui.cs.gatherlove.admin.security;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidationService jwtValidationService;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String jwt = extractJwtFromRequest(request);
        log.debug("JWT extracted: {}", jwt != null ? "present" : "not present");
        
        if (StringUtils.hasText(jwt) && jwtValidationService.validateToken(jwt)) {
            try {
                UserDTO user = authService.getUserFromToken(jwt);
                
                if (user != null) {
                    // Debug log untuk melihat roles
                    log.debug("User roles from token: {}", user.getRoles());
                    
                    // Buat authorities dari daftar roles
                    Collection<GrantedAuthority> authorities;
                    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                        authorities = user.getRoles().stream()
                            .map(role -> {
                                // Pastikan role memiliki prefix ROLE_
                                String authorityName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                                log.debug("Creating authority: {}", authorityName);
                                return new SimpleGrantedAuthority(authorityName);
                            })
                            .collect(Collectors.toList());
                    } else {
                        // Fallback jika roles kosong
                        String fallbackRole = user.getRole() != null ? user.getRole() : "USER";
                        String authorityName = fallbackRole.startsWith("ROLE_") ? fallbackRole : "ROLE_" + fallbackRole;
                        log.debug("Creating fallback authority: {}", authorityName);
                        authorities = Collections.singletonList(new SimpleGrantedAuthority(authorityName));
                    }
                    
                    log.debug("Final authorities: {}", authorities);
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            jwt,
                            authorities
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Set authentication for user: {}", user.getEmail());
                }
            } catch (Exception e) {
                log.error("Could not set user authentication in security context", e);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}