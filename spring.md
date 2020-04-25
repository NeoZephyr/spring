## 核心模块
spring-core: Spring 基础 API 模块，如资源管理、泛型处理
spring-beans: Spring Bean 相关，如依赖查找、依赖注入
spring-aop: Spring AOP 处理，如动态处理、AOP 字节码提升
spring-context: 事件驱动、注解驱动、模块驱动等
spring-expression: Spring 表达式语言模块


```java
BeanInfo beanInfo = Introspector.getBeanInfo(Person.class, Object.class);
Stream.of(beanInfo.getPropertyDescriptors()).forEach(propertyDescriptor -> {
    Class<?> propertyType = propertyDescriptor.getPropertyType();
    String propertyName = propertyDescriptor.getName();

    if ("age".equals(propertyName)) {
        propertyDescriptor.setPropertyEditorClass(StringToIntegerPropertyEditor.class);
    }
});

class StringToIntegerPropertyEditor extends PropertyEditorSupport {
    public void setAsText(String text) {
        Integer value = Integer.valueOf(text);
        setValue(value);
    }
}
```

SpringIOC 依赖查找