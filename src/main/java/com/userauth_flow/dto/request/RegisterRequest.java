package com.userauth_flow.dto.request;

import com.userauth_flow.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String fName;

    private String lName;

    private String email;

    private String password;

    private Role role;

}
