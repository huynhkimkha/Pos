package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.LicenseSearchDto;
import com.antdigital.agency.dtos.response.GeneralJournalReportDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface IGeneralJournalReportService {
    List<GeneralJournalReportDto> getOneGeneralJournalReport(HttpServletRequest request, LicenseSearchDto licenseSearchDto) throws IOException, JAXBException;
}
