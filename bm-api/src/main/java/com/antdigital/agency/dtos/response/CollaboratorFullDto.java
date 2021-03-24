package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.ActivatedStatusEnum;
import com.antdigital.agency.common.enums.BlockStatusEnum;
import com.antdigital.agency.common.enums.UserModelEnum;
import com.antdigital.agency.core.models.warehouse.SaleRankModel;
import com.antdigital.agency.dtos.response.security.UserDto;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CollaboratorFullDto {
    private String id;
    private AgencyDto agency;
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
    private SaleRankModel saleRank;
    private ActivatedStatusEnum activatedStatus;
    private BlockStatusEnum blockedStatus;
    private Date createdDate;
    private Date updatedDate;

    private ReferralBonusDto referee;
    private List<ReferralBonusDto> referralSales;

    public UserDto toUserDto() {
        List<String> permissions = new ArrayList<>();
        return new UserDto(this.fullName, this.email, this.password, permissions, null, null, UserModelEnum.COLLABORATOR);
    }
}
