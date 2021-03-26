package com.antdigital.agency.dtos.response.security;

import com.antdigital.agency.common.enums.UserModelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String fullname;
    private String email;
    private String password;
    private UserModelEnum userModel;
}
