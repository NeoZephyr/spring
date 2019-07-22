package com.pain.flame.bean;

public class Phone {
    private String color;
    private String brand;
    private int cores;

    public Phone() {
    }

    public Phone(String color, String brand, int cores) {
        this.color = color;
        this.brand = brand;
        this.cores = cores;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "color='" + color + '\'' +
                ", brand='" + brand + '\'' +
                ", cores=" + cores +
                '}';
    }
}
