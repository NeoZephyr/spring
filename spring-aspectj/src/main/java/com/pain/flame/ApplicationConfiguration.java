package com.pain.flame;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"com.pain.flame.service", "com.pain.flame.aspect"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ApplicationConfiguration {
    public ApplicationConfiguration() {
        System.out.println("=== Init application");
    }
}
