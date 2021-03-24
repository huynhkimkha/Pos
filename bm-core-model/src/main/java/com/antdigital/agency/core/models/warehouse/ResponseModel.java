package com.antdigital.agency.core.models.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseModel {
    List<String> message;
    int status;
    Object result;
}
