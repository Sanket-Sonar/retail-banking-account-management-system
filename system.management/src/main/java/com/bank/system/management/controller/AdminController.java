package com.bank.system.management.controller;

import com.bank.system.management.dto.CustomerResponse;
import com.bank.system.management.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Controller")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/customers")
    public List<CustomerResponse> getAllCustomers() {

        return adminService.getAllCustomers();

    }

}
