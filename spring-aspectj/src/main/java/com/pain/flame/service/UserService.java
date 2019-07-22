package com.pain.flame.service;

import java.util.List;

public interface UserService {
    void updateProfile();

    void pay();

    void incrPoint(String type, int point);

    List<String> listRoles();
}
