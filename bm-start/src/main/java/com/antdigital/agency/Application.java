package com.antdigital.agency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class Application {
    @Value("${spring.jpa.properties.hibernate.jdbc.time_zone}")
    private String configTimezone;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(configTimezone));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
