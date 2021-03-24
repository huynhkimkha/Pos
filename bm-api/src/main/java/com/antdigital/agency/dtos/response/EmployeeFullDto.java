package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.UserModelEnum;
import com.antdigital.agency.dtos.response.security.UserDto;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class EmployeeFullDto {
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
    @NotEmpty(message = "Chức vụ không được trống")
    @NotNull(message = "Chức vụ không được trống")
    private List<RoleDetailFullDto> roleDetails;

    private ReferralBonusDto referee;
    private List<ReferralBonusDto> referralSales;

    public UserDto toUserDto() {
        List<String> permissions = new ArrayList<>();
        List<RoleDetailFullDto> roleDetailDtoList = this.roleDetails;
        if (roleDetailDtoList != null && roleDetailDtoList.size() > 0){
            for(RoleDetailFullDto roleDetailDto : roleDetailDtoList) {
                if (roleDetailDto.getRole() == null) {
                    continue;
                }
                List<GrantPermissionDto> grantPermissionDtos = roleDetailDto.getRole().getGrantPermissions();
                for (GrantPermissionDto grantPermissionDto : grantPermissionDtos) {
                    String permission = grantPermissionDto.getPermission().getCode();
                    if(permissions.indexOf(permission) < 0) {
                        permissions.add(permission);
                    }
                }
            }
        }

        return new UserDto(this.fullName, this.email, this.password, permissions, null, this.agency.getId(), UserModelEnum.EMPLOYEE);
    }
}
