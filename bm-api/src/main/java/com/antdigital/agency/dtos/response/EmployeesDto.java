package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.UserModelEnum;
import com.antdigital.agency.dtos.response.security.UserDto;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class EmployeesDto {
    private String id;
    private AgencyDto agency;
    @NotEmpty(message = "Tên nhân viên không được trống")
    private String fullName;
    @NotEmpty(message = "Email nhân viên không được trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    @NotEmpty(message = "Mật khẩu không được trống")
    private String password;
    private Date birthDate;
    public UserDto toUserDto() {
        List<String> permissions = new ArrayList<>();

        // Handle permission here

        return new UserDto(this.fullName, this.email, this.password, permissions, null, null, UserModelEnum.EMPLOYEE);
    }
}
