package com.antdigital.agency.dtos.request;

import com.antdigital.agency.common.deserializers.TrimAllWhiteSpaceDeserialiser;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordDto {
    @JsonDeserialize(using = TrimAllWhiteSpaceDeserialiser.class)
    @NotNull(message = "Mật khẩu hiện tại không được để trống")
    @NotEmpty(message = "Mật khẩu hiện tại không được để trống")
    private String oldPassword;

    @JsonDeserialize(using = TrimAllWhiteSpaceDeserialiser.class)
    @NotNull(message = "Mật khẩu mới không được để trống")
    @NotEmpty(message = "Mật khẩu mới không được để trống")
    private String newPassword;

    @JsonDeserialize(using = TrimAllWhiteSpaceDeserialiser.class)
    @NotNull(message = "Mật khẩu khớp không được để trống")
    @NotEmpty(message = "Mật khẩu khớp không được để trống")
    private String confirmation;
}
