package com.example.ec.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/** 受注サービスの起動口。 */
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(OrderApplication.class, args);
        if (context.getEnvironment().matchesProfiles("batch")) {
            System.exit(SpringApplication.exit(context));
        }
    }
}
