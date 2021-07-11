package com.pain.green.aop.service;

public class HelloServiceImpl implements HelloService {
    @Override
    public void hello() {
        System.out.println("hello");
    }

    @Override
    public void morning() {
        System.out.println("morning");
    }
}
