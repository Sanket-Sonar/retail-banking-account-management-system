package com.bank.system.management.repository;

import com.bank.system.management.entity.User;
import com.bank.system.management.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser_whenEmailMatches() {
        User user = User.builder()
                .fullName("Jane Doe")
                .email("jane@gmail.com")
                .password("encoded")
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("jane@gmail.com");

        assertTrue(found.isPresent());
        assertEquals("Jane Doe", found.get().getFullName());
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenUserExists() {
        userRepository.save(User.builder()
                .fullName("John Doe")
                .email("john@gmail.com")
                .password("encoded")
                .role(Role.CUSTOMER)
                .build());

        assertTrue(userRepository.existsByEmail("john@gmail.com"));
        assertFalse(userRepository.existsByEmail("missing@gmail.com"));
    }

    @Test
    void findByRole_shouldReturnUsersForRole() {
        userRepository.saveAll(List.of(
                User.builder().fullName("Alice").email("alice@gmail.com").password("a").role(Role.CUSTOMER).build(),
                User.builder().fullName("Bob").email("bob@gmail.com").password("b").role(Role.CUSTOMER).build(),
                User.builder().fullName("Admin").email("admin@gmail.com").password("c").role(Role.ADMIN).build()
        ));

        List<User> customers = userRepository.findByRole(Role.CUSTOMER);

        assertEquals(2, customers.size());
        assertTrue(customers.stream().allMatch(user -> user.getRole() == Role.CUSTOMER));
    }
}
