## AOP
### 连接点
程序执行的某个特定位置

### 切入点
用于定位特定的连接点

### 增强
织入目标类连接点上的一段程序代码

### 切面
切入点与增强组成

### 织入时期
编译期织入：使用特殊的 java 编译器
类装载期织入：使用特殊的类装载器
动态代理织入：在运行期为目标类添加增强生成子类的方式

## Spring AOP
Spring AOP 采用动态代理方式织入

### JDK 动态代理
只能为接口创建代理实例

### CGLib 动态代理
不能对目标类中的 `final` 或 `private` 方法进行代理

### JDK 动态代理 VS CGLib 动态代理
CGLib 动态代理对象的性能比 JDK 动态代理创建对象的性能高，但 CGLib 在创建动态代理对象时花费时间比 JDK 动态代理多。对于无须频繁创建代理对象的情况下，使用 CGLib 比较合适

### 通知类型
前置通知、后置通知、环绕通知、抛出异常、引介

## AspectJ
采用编译期织入和类装载期织入

### 通知类型
@Before
@AfterReturning
@Around
@AfterThrowing
@After

开启 aspectj 自动代理
```xml
<aop:aspectj-autoproxy proxy-target-class="true" />
```
```
@EnableAspectJAutoProxy(proxyTargetClass = true)
```

### 切入点表达式
匹配所有 public 方法
```
execution(public * *(..))
```
匹配指定包下所有类方法，不包含子包
```
execution(* com.pain.flame.service.*(..))
```
匹配指定包下所有类方法，包含子包
```
execution(* com.pain.flame.service..*(..))
```
匹配指定类所有方法
```
execution(* com.pain.flame.service.UserService.*(..))
```
匹配实现特定接口所有类方法
```
execution(* com.pain.flame.service.GenericService+.*(..))
```
匹配所有 save 开头的方法
```
execution(* save*(..))
```

## AOP 无法增强问题
1. JDK 动态代理，必须确保要拦截的目标方法在接口中有定义，否则将无法实现拦截
2. CGLIB 动态代理，必须确保要拦截的目标方法可被子类访问，即目标方法必须定义为非私有实例方法
3. 方法内部之间的调用，不会使用被增强的代理类，而是直接调用未被增强原类的方法
