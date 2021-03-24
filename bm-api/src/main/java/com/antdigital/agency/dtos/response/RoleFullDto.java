package com.antdigital.agency.dtos.response;



import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
@Data
public class RoleFullDto {
    private String id;
    private AgencyDto agency;
    @NotNull (message = "Tên quyền không được để trống")
    @NotEmpty(message = "Tên quyền không được để trống")
    private String name;
    private Date createdDate;
    private Date updatedDate;
    private List<GrantPermissionDto> grantPermissions;
}
