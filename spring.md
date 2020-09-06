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


## 生命周期
### prepareRefresh
1. 启动时间 startupDate
2. 状态标识 closed(false), active(true)
3. 初始化 PropertySources initPropertySources()
4. 检验 Environment 中必须属性
5. 初始化事件监听器集合
6. 初始化早起 spring 事件集合

### obtainFreshBeanFactory
刷新 Spring 应用上下文底层 BeanFactory - refreshBeanFactory

1. 销毁或关闭 BeanFactory，如果已存在的话
2. 创建 BeanFactory，createBeanFactory()
3. 设置 BeanFactory id
4. 设置是否允许 BeanDefinition 重复定义
5. 设置是否允许循环引用
6. 加载 BeanDefinition
7. 关联新建 BeanFactory 到 Spring 应用上下文

返回 Spring 应用上下文底层 BeanFactory - getBeanFactory

### prepareBeanFactory
1. 关联 ClassLoader
2. 设置 Bean 表达式处理器
3. 添加 PropertyEditorRegistrar 实现 - ResourceEditorRegistrar
4. 添加 Aware 回调接口 BeanPostProcessor 实现 - ApplicationContextAwareProcessor
5. 忽略 Aware 回调接口作为依赖注入接口
6. 注册 ResolvableDependency 对象 - BeanFactory, ResourceLoader, ApplicationEventPublisher 以及 ApplicationContext
7. 注册 ApplicationListenerDetector 对象
8. 注册 LoadTimeWeaverAwareProcessor 对象
9. 注册单例对象 - Environment, Java System Properties 以及 OS 环境变量

### BeanFactory 后置处理
AbstractApplicationContext#postProcessBeanFactory(ConfigurableListableBeanFactory)
子类扩展

AbstractApplicationContext#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory)

### registerBeanPostProcessors
1. 注册 PriorityOrdered 类型的 BeanFactoryProcessor Beans
2. 注册 Ordered 类型的 BeanPostProcessor Beans
3. 注册普通 BeanPostProcessor Beans
4. 注册 MergedBeanDefinitionPostProcessor Beans
5. 注册 ApplicationListenerDetector 对象

### initMessageSource

### initApplicationEventMulticaster

### onRefresh

### registerListeners
1. 添加当前应用上下文所关联的 ApplicationListener 对象
2. 添加 BeanFactory 所注册 ApplicationListener Beans
3. 广播早期 Spring 事件

### finishBeanFactoryInitialization
1. BeanFactory 关联 ConversionService Bean，如果存在
2. 添加 StringValueResolver 对象
3. 依赖查找 LoadTimeWeaverAware Bean
4. BeanFactory 临时 ClassLoader 设置为 null
5. BeanFactory 冻结配置
6. BeanFactory 初始化非延迟单例 Beans

### finishRefresh
1. 清除 ResourceLoader 缓存 - clearResourceCaches()
2. 初始化 LifecycleProcessor 对象 - initLifecycleProcessor()
3. 调用 LifecycleProcessor#onRefresh 方法
4. 发布 Spring 应用上下文已刷新事件 - ContextRefreshedEvent
5. 向 MBeanServer 托管 Live Beans

### start
1. 启动 LifecycleProcessor。依赖查找 Lifecycle Beans，启动 Lifecycle Beans
2. 发布 Spring 应用上下文已启动事件 - ContextStartedEvent

### stop
1. 停止 LifecycleProcessor。依赖查找 Lifecycle Beans，停止 Lifecycle Beans
2. 发布 Spring 应用上下文已停止事件 - ContextStoppedEvent

### close
1. 状态标识：active(false)、closed(true)
2. Live Beans JMX 撤销托管 LiveBeansView.unregisterApplicationContext(ConfigurableApplicationContext)
3. 发布 Spring 应用上下文已关闭事件 ContextClosedEvent
4. 关闭 LifecycleProcessor。依赖查找 Lifecycle Beans，停止 Lifecycle Beans
5. 销毁 Spring Beans
6. 关闭 BeanFactory
7. 回调 onClose()
8. Shutdown Hook 线程