package com.piano.management;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.piano.management.mapper")
public class PianoApplication {
    public static void main(String[] args) {
        SpringApplication.run(PianoApplication.class, args);
    }
}
