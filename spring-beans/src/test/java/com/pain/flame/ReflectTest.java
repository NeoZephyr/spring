package com.pain.flame;

import com.pain.flame.bean.Phone;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Test
public class ReflectTest {

    public void test() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = classLoader.loadClass("com.pain.flame.bean.Phone");
        Constructor<?> constructor = clazz.getDeclaredConstructor((Class[]) null);
        Phone phone = (Phone) constructor.newInstance();

        Method method = clazz.getDeclaredMethod("setColor", String.class);
        method.invoke(phone, "blue");
        method = clazz.getDeclaredMethod("setBrand", String.class);
        method.invoke(phone, "iPhone");
        method = clazz.getDeclaredMethod("setCores", int.class);
        method.invoke(phone, 4);

        System.out.println(phone);
    }
}
