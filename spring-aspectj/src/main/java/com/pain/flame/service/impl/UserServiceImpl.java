package com.pain.flame.service.impl;

import com.pain.flame.annotation.Validate;
import com.pain.flame.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    public void updateProfile() {
        System.out.println("user update profile");
    }

    public void pay() {
        System.out.println("user pay");
        throw new RuntimeException("not support");
    }

    @Validate(value = true)
    public void incrPoint(String type, int point) {
        System.out.println(String.format("increment %d %s point for user", point, type));
    }

    public List<String> listRoles() {
        List<String> roles = new ArrayList<String>();
        roles.add("admin");
        roles.add("user");

        return roles;
    }
}
