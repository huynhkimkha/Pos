package com.antdigital.agency.configuration.security.jwt;

import com.antdigital.agency.common.enums.UserModelEnum;
import com.antdigital.agency.configuration.WebSecurityConfig;
import com.antdigital.agency.dtos.response.security.UserDto;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider tokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (request.getRequestURI().contains("/ws")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = getJwt(request);
            UserDto userModel = new UserDto();
            userModel.setAgencyId(getAgencyId(request));

            if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
                String userName = tokenProvider.getUserNameFromJwtToken(jwt);
                userModel.setEmail(userName);
                userModel.setPassword(tokenProvider.getPasswordFromJwtToken(jwt));
                userModel.setRole(tokenProvider.getPermissionFromJwToken(jwt));
                userModel.setAgencyId(tokenProvider.getAgencyIdFromJwtToken(jwt));
            }

            UserPrinciple userPrinciple = UserPrinciple.build(userModel);
            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(userPrinciple, null, userPrinciple.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.error("Invalid JWT signature -> Message: {} ", e);
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ","");
        }

        return null;
    }

    private String getAgencyId(HttpServletRequest request) {
        return request.getHeader("X-Agency");
    }

    private String getCompanyId(HttpServletRequest request) {
        return request.getHeader("X-Company");
    }

    private boolean checkAnonymousRequest (String uri) {
        for (String item: WebSecurityConfig.anonymousRequest) {
            boolean result = uri.contains(item);
            if(result) {
                return result;
            }
        }
        return false;
    }
}
