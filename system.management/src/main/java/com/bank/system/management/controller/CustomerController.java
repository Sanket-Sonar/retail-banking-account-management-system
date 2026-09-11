package com.bank.system.management.controller;


import com.bank.system.management.dto.CustomerResponse;
import com.bank.system.management.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Tag(name = "Customer Controller")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/profile")
    public CustomerResponse profile(Authentication authentication) {
        String email = authentication.getName();
        return customerService.getProfile(email);

    }

}
