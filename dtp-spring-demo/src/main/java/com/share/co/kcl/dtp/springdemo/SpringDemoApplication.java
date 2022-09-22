package com.share.co.kcl.dtp.springdemo;

import com.share.co.kcl.dtp.spring.EnableDynamicPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamicPool
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDemoApplication.class, args);
    }

}
