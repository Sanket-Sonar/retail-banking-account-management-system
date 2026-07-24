package com.bank.system.management.dto;

import com.bank.system.management.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {


    private String fullName;
    private String email;
    private String password;
    private Role role;


}
