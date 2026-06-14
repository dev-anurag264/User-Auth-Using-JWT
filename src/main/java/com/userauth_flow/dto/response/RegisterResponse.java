package com.userauth_flow.dto.response;

import com.userauth_flow.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private Long          id;
    private String        firstName;
    private String        lastName;
    private String        email;
    private Role role;
}
