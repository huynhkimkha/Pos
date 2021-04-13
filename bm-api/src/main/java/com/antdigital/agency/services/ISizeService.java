package com.antdigital.agency.services;

import com.antdigital.agency.dtos.response.SizeDto;

import java.util.List;

public interface ISizeService {
    List<SizeDto> findAll();
}
