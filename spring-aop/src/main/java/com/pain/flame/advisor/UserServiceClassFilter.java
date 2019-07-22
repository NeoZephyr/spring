package com.pain.flame.advisor;

import com.pain.flame.service.impl.UserServiceImpl;
import org.springframework.aop.ClassFilter;

public class UserServiceClassFilter implements ClassFilter {

    public boolean matches(Class<?> clazz) {
        if (clazz == UserServiceImpl.class) {
            return true;
        }
        return false;
    }
}
