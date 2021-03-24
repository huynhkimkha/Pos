package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.InvoiceCommission;
import com.antdigital.agency.dtos.response.InvoiceCommissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IInvoiceCommissionDtoMapper {
    IInvoiceCommissionDtoMapper INSTANCE = Mappers.getMapper(IInvoiceCommissionDtoMapper.class);
    InvoiceCommissionDto toInvoiceCommissionDto(InvoiceCommission invoiceCommission);
    InvoiceCommission toInvoiceCommission(InvoiceCommissionDto invoiceCommissionDto);
    List<InvoiceCommissionDto> toInvoiceCommissionList (List<InvoiceCommission> invoiceCommisions);
}
