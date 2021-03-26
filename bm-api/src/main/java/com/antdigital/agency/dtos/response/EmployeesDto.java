package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.UserModelEnum;
import com.antdigital.agency.dtos.response.security.UserDto;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class EmployeesDto {
    private String id;
    @NotEmpty(message = "Tên nhân viên không được trống")
    private String fullName;
    @NotEmpty(message = "Email nhân viên không được trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    @NotEmpty(message = "Mật khẩu không được trống")
    private String password;
    private Date birthDate;
    public UserDto toUserDto() {
        return new UserDto(this.fullName, this.email, this.password, UserModelEnum.EMPLOYEE);
    }
}
