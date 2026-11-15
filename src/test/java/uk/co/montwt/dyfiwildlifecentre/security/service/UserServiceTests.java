package uk.co.montwt.dyfiwildlifecentre.security.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.co.montwt.dyfiwildlifecentre.security.model.Role;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.model.repository.RoleRepository;
import uk.co.montwt.dyfiwildlifecentre.security.model.repository.UserRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void saveEncodesPasswordAssignsRolesAndSaves() {
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");

        User user = new User();
        user.setUsername("michael");
        user.setPassword("plainSecret");

        when(passwordEncoder.encode("plainSecret")).thenReturn("$2a$10$hashed");
        when(roleRepository.findAll()).thenReturn(List.of(adminRole));

        userService.save(user);

        Assertions.assertEquals("$2a$10$hashed", user.getPassword());
        Assertions.assertEquals(1, user.getRoles().size());
        Assertions.assertTrue(user.getRoles().contains(adminRole));
        verify(userRepository).save(user);
    }

    @Test
    void getAllSortsByUsernameAscending() {
        List<User> users = List.of(new User());
        when(userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"))).thenReturn(users);

        Assertions.assertEquals(users, userService.getAll());
    }

    @Test
    void deleteDelegatesToRepository() {
        userService.delete(10L);
        verify(userRepository).deleteById(10L);
    }
}
