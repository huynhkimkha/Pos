package com.antdigital.agency.configuration;

import com.antdigital.agency.configuration.security.jwt.JwtAuthEntryPoint;
import com.antdigital.agency.configuration.security.jwt.JwtAuthTokenFilter;
import com.antdigital.agency.configuration.security.jwt.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${adi.cors.client.agency.base-url}")
    private String clientDomain;

    @Value("${adi.cors.client.collaborators.base-url}")
    private String clientCollaboratorsDomain;

    @Value("${adi.services.warehouse.base-url}")
    private String warehouseDomain;

    @Value("${adi.cors.client.software.base-url}")
    private String clientSoftDomain;

    @Value("${adi.services.ezsoftware.base-url}")
    private String softDomain;



    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    public static String[] anonymousRequest = {
            "/auth/login",
            "/setting/findAll",
            "/collaborator/register",
            "/auth/login-collaborator",
            "/validate/check-exist-collaborator-employee\\?(.*)",
            "/validate/check-exist-collaborator-phone\\?(.*)",
            "/validate/check-exist-collaborator-email\\?(.*)",
            "/agency/getByCompanyId(.*)",
            "/agency/findOne(.*)",
            "/employee/findOne(.*)",
            "/collaborator/findOne(.*)",
            "/software/company/getByNameSlug\\?(.*)",
            "/software/company/getLikeNameOrSlugName\\?(.*)",
            "/software/company-license/validateLicense(.*)",
    };

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests =
                http.cors().and().csrf().disable().authorizeRequests();

        for (String anonymousRequest : anonymousRequest) {
            authorizeRequests.regexMatchers("/api/v(\\d+)" + anonymousRequest).permitAll();
            authorizeRequests.and();
        }

        authorizeRequests.regexMatchers("/api/v(\\d+)/(.*)").authenticated()
                .anyRequest().permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(clientDomain, clientCollaboratorsDomain, warehouseDomain, clientSoftDomain, softDomain));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
