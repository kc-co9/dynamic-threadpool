package com.share.co.kcl.threadpool.springdemo;

import com.share.co.kcl.threadpool.spring.EnableDynamicPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamicPool
@SpringBootApplication(scanBasePackages = "com.share.co.kcl.threadpool", exclude = {DataSourceAutoConfiguration.class})
public class SpringDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDemoApplication.class, args);
    }

}
