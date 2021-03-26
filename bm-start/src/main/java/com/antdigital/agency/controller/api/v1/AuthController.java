package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.common.enums.UserModelEnum;
import com.antdigital.agency.common.utils.BCryptHelper;
import com.antdigital.agency.configuration.security.jwt.JwtProvider;
import com.antdigital.agency.configuration.security.jwt.UserPrinciple;
import com.antdigital.agency.dtos.request.LoginDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.dtos.response.security.UserDto;
import com.antdigital.agency.services.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    private IEmployeeService employeeService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController() {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, @Valid @RequestBody LoginDto user) {
        logger.info("login api");

        EmployeesDto employeesDto = employeeService.getEmployeeByEmail(user.getUsername());
        if(employeesDto == null || !BCryptHelper.check(user.getPassword(), employeesDto.getPassword())) {
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tên đăng nhập hoặc password không đúng"), HttpStatus.BAD_GATEWAY.value(), ""));
        }

        UserDto userDto = employeesDto.toUserDto();
        userDto.setUserModel(UserModelEnum.EMPLOYEE);
        UserPrinciple userDetail = UserPrinciple.build(userDto);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetail, null, userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(SecurityContextHolder.getContext().getAuthentication());

        // Reset password
        userDto.setPassword("");

        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Đăng nhập thành công"), HttpStatus.OK.value(), new JwtResponseDto(jwt, userDto)));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Đăng xuất"),HttpStatus.OK.value(),""));
    }
}

