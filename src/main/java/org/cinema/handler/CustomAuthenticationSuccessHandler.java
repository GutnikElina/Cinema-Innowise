package org.cinema.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("Authentication successful for user: {}", authentication.getName());

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserResponseDTO> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            UserResponseDTO user = userOptional.get();

            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            log.info("User ID {} stored in session", user.getId());
        } else {
            log.warn("User {} not found in database", username);
        }

        String redirectUrl = determineTargetUrl(authentication);
        log.info("Successfully redirected to {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    private String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        authorities.forEach(authority -> log.info("Authority: {}", authority.getAuthority()));

        boolean isAdmin = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin ? "/Cinema/admin" : "/Cinema/user";
    }
}
