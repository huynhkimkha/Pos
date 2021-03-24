package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.common.enums.UserModelEnum;
import com.antdigital.agency.configuration.security.jwt.JwtProvider;
import com.antdigital.agency.configuration.security.jwt.MutableHttpServletRequest;
import com.antdigital.agency.configuration.security.jwt.UserPrinciple;
import com.antdigital.agency.dtos.response.security.UserDto;
import com.antdigital.agency.services.IDebtReportService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ScheduleController extends BaseController {

    @Autowired
    private IDebtReportService reportService;

    @Autowired
    private JwtProvider jwtProvider;

    @Scheduled(cron = "0 30 1 1 * *", zone = "Asia/Ho_Chi_Minh")
    public void calculateMonthlyClosingBalance() throws IOException, JAXBException, ParseException {
        List<String> tempPermissions = new ArrayList<>();
        tempPermissions.add("CUSTOMER_MANAGEMENT");
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        String jwt = generateToken(mockedRequest, new UserDto("fullName", "email", "password", tempPermissions, null, null, UserModelEnum.EMPLOYEE));
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(mockedRequest);
        mutableRequest.putHeader("Authorization", "Bearer " + jwt);
        // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // Date now = sdf.parse("01/12/2020");
        Calendar c1 = Calendar.getInstance();
        // c1.setTime(now);
        c1.add(Calendar.DATE, -1);
        Date toDate = new Date(c1.getTime().getTime());

        Calendar c2 = Calendar.getInstance();
        // c2.setTime(now);
        c2.add(Calendar.DATE, -1);
        int complementFromDate = c2.get(Calendar.DAY_OF_MONTH);
        c2.add(Calendar.DATE, -complementFromDate + 1);
        Date fromDate = new Date(c2.getTime().getTime());
        reportService.saveMonthlyClosingBalance(mutableRequest, fromDate, toDate, this.getAgencyId());
    }

    public String generateToken(HttpServletRequest request, UserDto userDto) {
        UserPrinciple userDetail = UserPrinciple.build(userDto);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetail, null, userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(SecurityContextHolder.getContext().getAuthentication());

        return jwt;
    }
}
