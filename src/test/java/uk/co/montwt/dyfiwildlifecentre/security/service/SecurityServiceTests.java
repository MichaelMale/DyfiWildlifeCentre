package uk.co.montwt.dyfiwildlifecentre.security.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private SecurityService securityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void autoLoginAuthenticatesAndSetsSecurityContext() {
        UserDetails details = new User(
                "adminuser",
                "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(userDetailsService.loadUserByUsername("adminuser")).thenReturn(details);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        securityService.autoLogin("adminuser", "rawpassword");

        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNotNull(current);
        Assertions.assertTrue(current.isAuthenticated());
        Assertions.assertEquals(details, current.getPrincipal());
        Assertions.assertEquals("rawpassword", current.getCredentials());
    }

    @Test
    void autoLoginPropagatesAuthenticationFailure() {
        UserDetails details = new User(
                "adminuser",
                "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(userDetailsService.loadUserByUsername("adminuser")).thenReturn(details);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        Assertions.assertThrows(BadCredentialsException.class,
                () -> securityService.autoLogin("adminuser", "wrong"));
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
