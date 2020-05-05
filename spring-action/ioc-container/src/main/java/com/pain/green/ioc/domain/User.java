package com.pain.green.ioc.domain;

import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.List;

public class User {
    private Long id;
    private String name;
    private City city;
    private City[] workCities;
    private List<City> schoolCities;
    private Resource configLocation;

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public City[] getWorkCities() {
        return workCities;
    }

    public void setWorkCities(City[] workCities) {
        this.workCities = workCities;
    }

    public List<City> getSchoolCities() {
        return schoolCities;
    }

    public void setSchoolCities(List<City> schoolCities) {
        this.schoolCities = schoolCities;
    }

    public Resource getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city=" + city +
                ", workCities=" + Arrays.toString(workCities) +
                ", schoolCities=" + schoolCities +
                ", configLocation=" + configLocation +
                '}';
    }

    public static User createUser() {
        User user = new User();
        user.setId(4L);
        user.setName("贺拔岳");
        return user;
    }
}
