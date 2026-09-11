package com.bank.system.management.security;

import com.bank.system.management.service.CustomUserDetailsService;
import com.bank.system.management.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilter_WhenAuthorizationHeaderIsNull()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldContinueFilter_WhenHeaderDoesNotStartWithBearer()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Basic test");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldAuthenticateUser_WhenTokenIsValid()
            throws Exception {

        String token = "jwt-token";
        String email = "test@gmail.com";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token);

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        UserDetails userDetails =
                User.withUsername(email)
                        .password("password")
                        .roles("CUSTOMER")
                        .build();

        when(jwtService.extractEmail(token))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                token,
                userDetails))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        assertNotNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());

        assertEquals(
                email,
                ((UserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal())
                        .getUsername());

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticate_WhenTokenIsInvalid()
            throws Exception {

        String token = "jwt-token";
        String email = "test@gmail.com";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token);

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        UserDetails userDetails =
                User.withUsername(email)
                        .password("password")
                        .roles("CUSTOMER")
                        .build();

        when(jwtService.extractEmail(token))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                token,
                userDetails))
                .thenReturn(false);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldSkipAuthentication_WhenAlreadyAuthenticated()
            throws Exception {

        UsernamePasswordAuthenticationToken existingAuth =
                new UsernamePasswordAuthenticationToken(
                        "existing-user",
                        null,
                        java.util.Collections.emptyList());

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuth);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(jwtService.extractEmail("token"))
                .thenReturn("test@gmail.com");

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(userDetailsService, never())
                .loadUserByUsername(anyString());

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldHandleExceptionAndContinueFilter()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(jwtService.extractEmail("token"))
                .thenThrow(
                        new RuntimeException("JWT Error"));

        assertDoesNotThrow(() ->
                filter.doFilterInternal(
                        request,
                        response,
                        filterChain));

        verify(filterChain)
                .doFilter(request, response);
    }
}