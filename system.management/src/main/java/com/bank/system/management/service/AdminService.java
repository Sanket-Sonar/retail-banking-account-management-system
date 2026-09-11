package com.bank.system.management.service;

import com.bank.system.management.dto.CustomerResponse;
import com.bank.system.management.entity.User;
import com.bank.system.management.enums.Role;
import com.bank.system.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public List<CustomerResponse> getAllCustomers() {

        List<User> customers =
                userRepository.findByRole(Role.CUSTOMER);

        return customers.stream()
                .map(user -> CustomerResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build())
                .toList();
    }
}
