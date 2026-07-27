package com.bank.system.management.dto;

import com.bank.system.management.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 50,
                message = "Full name must be between 3 and 50 characters")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$",
                message = "Only Gmail addresses are allowed")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 20,
                message = "Password must be between 8 and 20 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message =
                        "Password must contain uppercase, lowercase, number and special character")
        private String password;

        @NotNull(message = "Role is required")
        private Role role;
    }


