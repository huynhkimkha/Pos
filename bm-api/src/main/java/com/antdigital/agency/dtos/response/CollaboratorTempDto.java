package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.ActivatedStatusEnum;
import com.antdigital.agency.common.enums.BlockStatusEnum;
import com.antdigital.agency.common.enums.UserModelEnum;
import com.antdigital.agency.core.models.warehouse.SaleRankModel;
import com.antdigital.agency.dtos.response.security.UserDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.antdigital.agency.dtos.response.security.UserDto;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CollaboratorTempDto {
    private String id;
    @NotEmpty(message = "Tên cộng tác viên không được trống")
    private String fullName;
    @NotEmpty(message = "Email cộng tác viên không được trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    @NotEmpty(message = "Mật khẩu không được trống")
    private String password;
    private Date birthDate;
    private String address;
    private String district;
    private String province;
    private String phone;
    private String saleRankId;
    private String activatedStatus;
    private String blockedStatus;
    private Date createdDate;
    private Date updatedDate;

    public UserDto toUserDto() {
        List<String> permissions = new ArrayList<>();
        return new UserDto(this.fullName, this.email, this.password, permissions, null, null, UserModelEnum.COLLABORATOR);
    }
}
