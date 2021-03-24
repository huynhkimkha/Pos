package com.antdigital.agency.dtos.response;

import com.antdigital.agency.dtos.response.security.UserDto;
import lombok.Data;

@Data
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private UserDto user;

    public JwtResponseDto(String accessToken, UserDto user) {
        this.token = accessToken;
        this.user = user;
    }
}
