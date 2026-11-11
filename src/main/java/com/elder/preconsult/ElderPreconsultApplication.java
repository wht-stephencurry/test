package com.elder.preconsult;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.elder.preconsult.mapper")
public class ElderPreconsultApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderPreconsultApplication.class, args);
    }
}
