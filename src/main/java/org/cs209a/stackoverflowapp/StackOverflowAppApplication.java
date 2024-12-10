package org.cs209a.stackoverflowapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.cs209a.stackoverflowapp.mapper")
public class StackOverflowAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StackOverflowAppApplication.class, args);
    }

}
