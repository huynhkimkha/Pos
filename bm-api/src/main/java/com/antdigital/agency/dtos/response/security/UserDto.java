package com.antdigital.agency.dtos.response.security;

import com.antdigital.agency.common.enums.UserModelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String fullname;
    private String email;
    private String password;
    private List<String> permissions;
    private String companyId;
    private String agencyId;
    private UserModelEnum userModel;
}
