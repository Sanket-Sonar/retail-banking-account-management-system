package com.bank.system.management.security;

import com.bank.system.management.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private SecurityConfig securityConfig;

    private HttpSecurity httpSecurity;

    @BeforeEach
    void setUp() {
        httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {

        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {

        AuthenticationProvider provider =
                securityConfig.authenticationProvider();

        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManager()
            throws Exception {

        AuthenticationManager manager =
                mock(AuthenticationManager.class);

        when(authenticationConfiguration.getAuthenticationManager())
                .thenReturn(manager);

        AuthenticationManager result =
                securityConfig.authenticationManager(
                        authenticationConfiguration);

        assertNotNull(result);
        assertEquals(manager, result);

        verify(authenticationConfiguration)
                .getAuthenticationManager();
    }

    @Test
    void securityFilterChain_ShouldBuildSuccessfully() throws Exception {

        DefaultSecurityFilterChain filterChain =
                mock(DefaultSecurityFilterChain.class);

        when(httpSecurity.csrf(any()))
                .thenReturn(httpSecurity);

        when(httpSecurity.sessionManagement(any()))
                .thenReturn(httpSecurity);

        when(httpSecurity.authorizeHttpRequests(any()))
                .thenReturn(httpSecurity);

        when(httpSecurity.addFilterBefore(
                any(),
                eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(httpSecurity);

        when(httpSecurity.authenticationProvider(any()))
                .thenReturn(httpSecurity);

        when(httpSecurity.build())
                .thenReturn(filterChain);

        SecurityFilterChain result =
                securityConfig.securityFilterChain(httpSecurity);

        assertNotNull(result);

        verify(httpSecurity).build();
    }
}