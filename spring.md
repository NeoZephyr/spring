## 核心模块
spring-core: Spring 基础 API 模块，如资源管理、泛型处理
spring-beans: Spring Bean 相关，如依赖查找、依赖注入
spring-aop: Spring AOP 处理，如动态处理、AOP 字节码提升
spring-context: 事件驱动、注解驱动、模块驱动等
spring-expression: Spring 表达式语言模块


## BeanFactory vs FactoryBean
BeanFactory: IoC 底层容器
FactoryBean: 创建 Bean 的方式，帮助实现复杂的初始化逻辑


## Spring IoC 容器的启动
1. IoC 配置元信息读取与解析
2. IoC 容器生命周期
3. Spring 事件发布
4. 国际化


## BeanDefinition
BeanDefinition 是 Spring Framework 中定义 Bean 的配置元信息接口。包含：
1. Bean 类名
2. Bean 行为配置元素，如作用域、自动绑定的模式、生命周期回调等
3. 其他 Bean 引用，又可称为合作者或者依赖
4. 配置设置，比如 Bean 属性


BeanDefinition 元信息
Class: Bean 全类名，必须是具体类，不能用抽象类或者接口
Name: Bean 的名称或者 id
Scope: Bean 的作用域
Constructor arguments: Bean 构造器参数
Properties: Bean 属性设置
Autowiring mode: Bean 自动绑定模式
Lazy initialization mode: Bean 延迟初始化模式
Initialization method: Bean 初始化回调方法名称
Destruction method: Bean 销毁回调方法名称


Bean 命名
BeanNameGenerator
DefaultBeanNameGenerator
AnnotationBeanNameGenerator


BeanDefinition 注册
XML 配置元信息
Java 注解配置元信息
Java API 配置元信息


## 实例化 Bean
常规方式：
通过构造器
通过静态工厂方法
通过 Bean 工厂方法
通过 FactoryBean

特殊方式：
通过 ServiceLoaderFactoryBean
通过 AutowireCapableBeanFactory#createBean
通过 BeanDefinitionRegistry#registerBeanDefinition


## 依赖注入模式
手动模式
1. xml 资源配置元信息
2. java 注解配置元信息
3. api 配置元信息

自动模式
1. Autowiring



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