package com.dev.tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class ToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolApplication.class,args);
    }
}
