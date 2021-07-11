package com.pain.green.aop.advisor;

import com.pain.green.aop.service.HelloService;
import com.pain.green.aop.service.HelloServiceImpl;
import org.springframework.aop.ClassFilter;

public class HelloServiceClassFilter implements ClassFilter {
    @Override
    public boolean matches(Class<?> clazz) {
        if (clazz == HelloServiceImpl.class) {
            return true;
        }
        return false;
    }
}
