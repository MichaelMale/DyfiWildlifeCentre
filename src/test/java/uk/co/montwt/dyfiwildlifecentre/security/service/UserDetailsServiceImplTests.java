package uk.co.montwt.dyfiwildlifecentre.security.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.co.montwt.dyfiwildlifecentre.security.model.Role;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.model.repository.UserRepository;

import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsernameReturnsSpringSecurityUserWithAuthorities() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setUsername("staffuser");
        user.setPassword("$2a$10$encoded");
        user.setRoles(Set.of(role));

        when(userRepository.findByUsername("staffuser")).thenReturn(user);

        UserDetails details = userDetailsService.loadUserByUsername("staffuser");
        Assertions.assertEquals("staffuser", details.getUsername());
        Assertions.assertEquals("$2a$10$encoded", details.getPassword());
        Assertions.assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsernameThrowsWhenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing"));
    }
}
