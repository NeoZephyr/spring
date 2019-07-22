## BeanFactory 与 ApplicationContext
1. `BeanFactory` 初始化容器时，并未实例化 `Bean`，直到第一次访问某个 `Bean` 时才实例化目标 `Bean`；`ApplicationContext` 在初始化上下文时就实例化所有单实例的 `Bean`
2. `ApplicationContext` 会利用反射机制自动识别出配置文件中定义的 `BeanPostProcessor`, `InstantiationAwareBeanPostProcessor`, `BeanFactoryPostProcessor`，并自动将它们注册到应用上下文中

## 工厂后处理器
### `PropertyPlaceholderConfigurer`
引用外部属性文件，通常使用 `context:property-placeholder`
```xml
<context:property-placeholder location="classpath:jdbc.properties" />
```

如果需要自定义一些额外高级功能（比如加密配置文件），就必须扩展 `PropertyPlaceholderConfigurer`，使 Bean 在配置时引用外部属性文件，`PropertyPlaceholderConfigurer` 实现了 `BeanFactoryPostProcessorBean` 接口，属于工厂后处理器
```xml
<bean id="placeholderConfigurer" class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
    <property name="ignoreUnresolvablePlaceholders" value="true" />
    <property name="locations">
        <list>
            <value>classpath:database.properties</value>
        </list>
    </property>
    <property name="fileEncoding" value="UTF-8" />
</bean>
```

`PropertyPlaceholderConfigurer` 继承 `PropertyResourceConfigurer`，后者提供方法用于在属性使用前对属性列表中的属性进行转换

### 加密工具
```java
public class DESUtils {
  private static Key key;
  private static String KEY_STR = "myKey";

  static {
    try {
      KeyGenerator generator = KeyGenerator.getInstance("DES");
      generator.init(new SecureRandom(KEY_STR.getBytes()));
      key = generator.generateKey();
      generator = null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String getEncryptString(String str) {
    BASE64Encoder base64en = new BASE64Encoder();
    try {
      byte[] strBytes = str.getBytes("UTF8");
      Cipher cipher = Cipher.getInstance("DES");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encryptStrBytes = cipher.doFinal(strBytes);
      return base64en.encode(encryptStrBytes);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String getDecryptString(String str) {
    BASE64Decoder base64De = new BASE64Decoder();
    try {
      byte[] strBytes = base64De.decodeBuffer(str);
      Cipher cipher = Cipher.getInstance("DES");
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decryptStrBytes = cipher.doFinal(strBytes);
      return new String(decryptStrBytes, "UTF8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    for (String arg : args) {
      System.out.println(arg + ":" + getEncryptString(arg));
    }
  }
}
```

```java
public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
  private String[] encryptPropNames = {"username", "password"};

  protected String convertProperty(String propertyName, String propertyValue) {
    if (isEncryptProp(propertyName)) {
      String decryptValue = DESUtils.getDecryptString(propertyValue);
      System.out.println(decryptValue);
      return decryptValue;
    } else {
      return propertyValue;
    }
  }

  private boolean isEncryptProp(String propertyName) {
    for (String encryptPropName : encryptPropNames) {
      if (encryptPropName.equals(propertyName)) {
        return true;
      }
    }
    return false;
  }
}
```

```xml
<bean class="com.pain.placeholder.EncryptPropertyPlaceholderConfigurer"
  p:location="classpath:jdbc.properties"
  p:fileEncoding="utf-8" />
```

## 注解
### IOC 注解
```xml
<context:component-scan base-package="" />
```

#### `@Component`
相当于 `<bean>` 标签

#### `@Controller`

#### `@Service`

#### `@Repository`

### DI 注解
#### `@Autowired`
默认按照类型装配，可以与 `@Qualifier` 配合使用，在按照类型注入的基础上，再指定名称

#### `@Resource`
默认按照名称装配

#### `@Value`
属性注入，相当于 `<property name='' value=''>`

```xml
<!-- 打开属性注入注解 -->
<context:annotation-config />
```

#### `@Scope`

#### `@PostConstruct`

#### `@PreDestroy`

#### `@Configuration`
相当于 `<beans>` 标签

#### `@Bean`
相当于 `<bean>` 标签

#### `@ComponentScan`
相当于 `<context:component-scan>` 标签，一般配合 `@Configuration` 使用

#### `@PropertySource`
相当于 `<context:property-placeholder>` 标签，加载 properties 配置文件

#### `@Import`
相当于 `<import>` 标签，引入其他配置类

## 创建容器流程
### 步骤一
```java
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans.xml");
```

### 步骤二
创建 `ClassPathXmlApplicationContext`，从 xml 文件中加载相关定义，然后自动刷新 `ClassPathXmlApplicationContext`
```java
// ClassPathXmlApplicationContext 类
public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
    this(new String[]{configLocation}, true, (ApplicationContext)null);
}

public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, @Nullable ApplicationContext parent) throws BeansException {
    // 主要就是设置父容器
    super(parent);

    this.setConfigLocations(configLocations);
    if (refresh) {
        this.refresh();
    }
}
```
```java
// AbstractApplicationContext 类
public void refresh() throws BeansException, IllegalStateException {
    synchronized(this.startupShutdownMonitor) {
        prepareRefresh();

        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
        prepareBeanFactory(beanFactory);

        try {
            postProcessBeanFactory(beanFactory);

            // 根据反射机制从 BeanDefinitionRegistry 中找出所有实现 BeanFactoryPostProcessor 接口的 Bean，实例化并调用其 postProcessBeanFactory 接口方法，主要针对 BeanDefinition 进行处理
            // 例如，PropertyPlaceholderConfigurer 就是替换掉 BeanDefinition 中的占位符
            invokeBeanFactoryPostProcessors(beanFactory);

            // 根据反射机制从 BeanDefinitionRegistry 中找出所有实现 BeanPostProcessor 接口的 Bean，实例化并将它们注册到容器 Bean 后处理器的注册表中
            this.registerBeanPostProcessors(beanFactory);

            // 初始化消息源
            this.initMessageSource();

            // 初始化应用上下文事件广播器
            this.initApplicationEventMulticaster();

            // 初始化其他特殊 Bean，具体由子类实现
            this.onRefresh();

            // 注册事件监听器
            this.registerListeners();

            // 创建非懒加载方式的单例 Bean，填充属性，初始化实例
            // 调用 BeanPostProcessor 对实例进行后置处理
            this.finishBeanFactoryInitialization(beanFactory);

            // 完成刷新并发布容器刷新事件
            this.finishRefresh();
        } catch (BeansException ex) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + ex);
            }

            this.destroyBeans();
            this.cancelRefresh(ex);
            throw ex;
        } finally {
            this.resetCommonCaches();
        }

    }
}
```

### 步骤三，`BeanFactory` 子流程
初始化 `BeanFactory`，将配置文件信息，加载 `BeanDefinition` 并注册到 `BeanDefinitionRegistry` 中，通过 `NamespaceHandler` 解析标签

1. 创建底层 `BeanFactory`
```java
// AbstractApplicationContext 类

// 告知子类 refresh 内部 bean factory
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
    this.refreshBeanFactory();
    return this.getBeanFactory();
}

// AbstractRefreshableApplicationContext 类
protected final void refreshBeanFactory() throws BeansException {
    if (this.hasBeanFactory()) {
        this.destroyBeans();
        this.closeBeanFactory();
    }

    try {
        // 返回一个内部的 BeanFactory 实例
        DefaultListableBeanFactory beanFactory = this.createBeanFactory();
        beanFactory.setSerializationId(this.getId());
        this.customizeBeanFactory(beanFactory);

        // 加载应用上下文中的 BeanDefinition 信息
        this.loadBeanDefinitions(beanFactory);
        synchronized(this.beanFactoryMonitor) {
            this.beanFactory = beanFactory;
        }
    } catch (IOException ex) {
        throw new ApplicationContextException("I/O error parsing bean definition source for " + this.getDisplayName(), ex);
    }
}
```

2. 加载 `BeanDefinition` 信息
```java
// AbstractXmlApplicationContext 类
protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
    beanDefinitionReader.setEnvironment(this.getEnvironment());
    beanDefinitionReader.setResourceLoader(this);
    beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
    this.initBeanDefinitionReader(beanDefinitionReader);
    this.loadBeanDefinitions(beanDefinitionReader);
}

protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
    Resource[] configResources = this.getConfigResources();
    if (configResources != null) {
        reader.loadBeanDefinitions(configResources);
    }

    String[] configLocations = this.getConfigLocations();
    if (configLocations != null) {
        reader.loadBeanDefinitions(configLocations);
    }

}
```
```java
// AbstractBeanDefinitionReader 类
public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
    Assert.notNull(locations, "Location array must not be null");
    int count = 0;

    for(int i = 0; i < locations.length; ++i) {
        String location = locations[i];
        count += this.loadBeanDefinitions(location);
    }

    return count;
}
```
```java
// XmlBeanDefinitionReader 类
public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
    return this.loadBeanDefinitions(location, (Set)null);
}

public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
    ResourceLoader resourceLoader = this.getResourceLoader();
    if (resourceLoader == null) {
        throw new BeanDefinitionStoreException("Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
    } else {
        int count;
        if (resourceLoader instanceof ResourcePatternResolver) {
            try {
                Resource[] resources = ((ResourcePatternResolver)resourceLoader).getResources(location);
                count = this.loadBeanDefinitions(resources);
                if (actualResources != null) {
                    Collections.addAll(actualResources, resources);
                }

                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
                }

                return count;
            } catch (IOException ex) {
                throw new BeanDefinitionStoreException("Could not resolve bean definition resource pattern [" + location + "]", ex);
            }
        } else {
            Resource resource = resourceLoader.getResource(location);
            count = this.loadBeanDefinitions((Resource)resource);
            if (actualResources != null) {
                actualResources.add(resource);
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
            }

            return count;
        }
    }
}

public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
    Assert.notNull(resources, "Resource array must not be null");
    int count = 0;

    for(int i = 0; i < resources.length; ++i) {
        Resource resource = resources[i];
        count += this.loadBeanDefinitions((Resource)resource);
    }

    return count;
}
```
```java
// XmlBeanDefinitionReader 类
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
    return this.loadBeanDefinitions(new EncodedResource(resource));
}

public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
    Assert.notNull(encodedResource, "EncodedResource must not be null");
    if (this.logger.isTraceEnabled()) {
        this.logger.trace("Loading XML bean definitions from " + encodedResource);
    }

    Set<EncodedResource> currentResources = (Set)this.resourcesCurrentlyBeingLoaded.get();
    if (currentResources == null) {
        currentResources = new HashSet(4);
        this.resourcesCurrentlyBeingLoaded.set(currentResources);
    }

    if (!((Set)currentResources).add(encodedResource)) {
        throw new BeanDefinitionStoreException("Detected cyclic loading of " + encodedResource + " - check your import definitions!");
    } else {
        int count;
        try {
            InputStream inputStream = encodedResource.getResource().getInputStream();

            try {
                InputSource inputSource = new InputSource(inputStream);
                if (encodedResource.getEncoding() != null) {
                    inputSource.setEncoding(encodedResource.getEncoding());
                }

                count = this.doLoadBeanDefinitions(inputSource, encodedResource.getResource());
            } finally {
                inputStream.close();
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("IOException parsing XML document from " + encodedResource.getResource(), ex);
        } finally {
            ((Set)currentResources).remove(encodedResource);
            if (((Set)currentResources).isEmpty()) {
                this.resourcesCurrentlyBeingLoaded.remove();
            }

        }

        return count;
    }
}

// 真正地加载 bean definitions
protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource) throws BeanDefinitionStoreException {
    try {

        // 将 xml 文档信息保存到 Document 对象中
        Document doc = this.doLoadDocument(inputSource, resource);

        // 解析 Document 获取 BeanDefinition 信息，并进行注册
        int count = this.registerBeanDefinitions(doc, resource);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Loaded " + count + " bean definitions from " + resource);
        }

        return count;
    } catch (BeanDefinitionStoreException var5) {
        throw var5;
    } catch (SAXParseException var6) {
        throw new XmlBeanDefinitionStoreException(resource.getDescription(), "Line " + var6.getLineNumber() + " in XML document from " + resource + " is invalid", var6);
    } catch (SAXException var7) {
        throw new XmlBeanDefinitionStoreException(resource.getDescription(), "XML document from " + resource + " is invalid", var7);
    } catch (ParserConfigurationException var8) {
        throw new BeanDefinitionStoreException(resource.getDescription(), "Parser configuration exception parsing XML from " + resource, var8);
    } catch (IOException var9) {
        throw new BeanDefinitionStoreException(resource.getDescription(), "IOException parsing XML document from " + resource, var9);
    } catch (Throwable var10) {
        throw new BeanDefinitionStoreException(resource.getDescription(), "Unexpected exception parsing XML document from " + resource, var10);
    }
}

public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
    BeanDefinitionDocumentReader documentReader = this.createBeanDefinitionDocumentReader();
    int countBefore = this.getRegistry().getBeanDefinitionCount();

    // 获取 NamespaceResolver，并通过 NamespaceResolver 查找合适的 NamespaceHandler 解析文档，得到 BeanDefinition，完成注册
    documentReader.registerBeanDefinitions(doc, this.createReaderContext(resource));
    return this.getRegistry().getBeanDefinitionCount() - countBefore;
}

public XmlReaderContext createReaderContext(Resource resource) {
    return new XmlReaderContext(resource, this.problemReporter, this.eventListener, this.sourceExtractor, this, this.getNamespaceHandlerResolver());
}

public NamespaceHandlerResolver getNamespaceHandlerResolver() {
    if (this.namespaceHandlerResolver == null) {
        this.namespaceHandlerResolver = this.createDefaultNamespaceHandlerResolver();
    }

    return this.namespaceHandlerResolver;
}

protected NamespaceHandlerResolver createDefaultNamespaceHandlerResolver() {
    ClassLoader cl = this.getResourceLoader() != null ? this.getResourceLoader().getClassLoader() : this.getBeanClassLoader();

    // 使用默认映射文件 spring.handlers 创建，内容如下：

    // http\://www.springframework.org/schema/c=org.springframework.beans.factory.xml.SimpleConstructorNamespaceHandler
    // http\://www.springframework.org/schema/p=org.springframework.beans.factory.xml.SimplePropertyNamespaceHandler
    // http\://www.springframework.org/schema/util=org.springframework.beans.factory.xml.UtilNamespaceHandler
    return new DefaultNamespaceHandlerResolver(cl);
}
```
```java
// DefaultBeanDefinitionDocumentReader 类
public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
    this.readerContext = readerContext;
    this.doRegisterBeanDefinitions(doc.getDocumentElement());
}

// 注册 <beans> 标签下的每一个 bean definition
protected void doRegisterBeanDefinitions(Element root) {
    BeanDefinitionParserDelegate parent = this.delegate;
    this.delegate = this.createDelegate(this.getReaderContext(), root, parent);
    if (this.delegate.isDefaultNamespace(root)) {
        String profileSpec = root.getAttribute("profile");
        if (StringUtils.hasText(profileSpec)) {
            String[] specifiedProfiles = StringUtils.tokenizeToStringArray(profileSpec, ",; ");
            if (!this.getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec + "] not matching: " + this.getReaderContext().getResource());
                }

                return;
            }
        }
    }

    this.preProcessXml(root);

    // 解析出文档中的 BeanDefinition
    this.parseBeanDefinitions(root, this.delegate);
    this.postProcessXml(root);
    this.delegate = parent;
}

protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
    if (delegate.isDefaultNamespace(root)) {
        NodeList nl = root.getChildNodes();

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element)node;
                if (delegate.isDefaultNamespace(ele)) {
                    // 解析默认元素
                    this.parseDefaultElement(ele, delegate);
                } else {
                    // 解析自定义元素
                    delegate.parseCustomElement(ele);
                }
            }
        }
    } else {
        // 解析自定义元素
        delegate.parseCustomElement(root);
    }

}

private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
    if (delegate.nodeNameEquals(ele, "import")) {
        this.importBeanDefinitionResource(ele);
    } else if (delegate.nodeNameEquals(ele, "alias")) {
        this.processAliasRegistration(ele);
    } else if (delegate.nodeNameEquals(ele, "bean")) {
        // 建立 beanName -> beanDefinition 关联关系
        this.processBeanDefinition(ele, delegate);
    } else if (delegate.nodeNameEquals(ele, "beans")) {
        this.doRegisterBeanDefinitions(ele);
    }
}
```
```java
// BeanDefinitionParserDelegate 类
@Nullable
public BeanDefinition parseCustomElement(Element ele) {
    return this.parseCustomElement(ele, (BeanDefinition)null);
}

@Nullable
public BeanDefinition parseCustomElement(Element ele, @Nullable BeanDefinition containingBd) {
    // 根据指定元素，找到 beans 根标签中 xmlns:xxx 属性值
    String namespaceUri = this.getNamespaceURI(ele);
    if (namespaceUri == null) {
        return null;
    } else {

        // 获取 NamespaceHandler
        NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
        if (handler == null) {
            this.error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
            return null;
        } else {

            // 执行解析
            return handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
        }
    }
}
```
```java
// DefaultNamespaceHandlerResolver 类
@Nullable
public NamespaceHandler resolve(String namespaceUri) {
    Map<String, Object> handlerMappings = this.getHandlerMappings();
    Object handlerOrClassName = handlerMappings.get(namespaceUri);
    if (handlerOrClassName == null) {
        return null;
    } else if (handlerOrClassName instanceof NamespaceHandler) {
        return (NamespaceHandler)handlerOrClassName;
    } else {
        String className = (String)handlerOrClassName;

        try {
            Class<?> handlerClass = ClassUtils.forName(className, this.classLoader);
            if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
                throw new FatalBeanException("Class [" + className + "] for namespace [" + namespaceUri + "] does not implement the [" + NamespaceHandler.class.getName() + "] interface");
            } else {
                NamespaceHandler namespaceHandler = (NamespaceHandler)BeanUtils.instantiateClass(handlerClass);

                // 初始化自定义标签对应的功能处理组件
                namespaceHandler.init();
                handlerMappings.put(namespaceUri, namespaceHandler);
                return namespaceHandler;
            }
        } catch (ClassNotFoundException var7) {
            throw new FatalBeanException("Could not find NamespaceHandler class [" + className + "] for namespace [" + namespaceUri + "]", var7);
        } catch (LinkageError var8) {
            throw new FatalBeanException("Unresolvable class definition for NamespaceHandler class [" + className + "] for namespace [" + namespaceUri + "]", var8);
        }
    }
}
```
```java
// AopNamespaceHandler 类
public void init() {
    this.registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
    this.registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
    this.registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());
    this.registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
}
```
```java
// NamespaceHandlerSupport 类
@Nullable
public BeanDefinition parse(Element element, ParserContext parserContext) {
    BeanDefinitionParser parser = this.findParserForElement(element, parserContext);
    return parser != null ? parser.parse(element, parserContext) : null;
}

@Nullable
private BeanDefinitionParser findParserForElement(Element element, ParserContext parserContext) {
    String localName = parserContext.getDelegate().getLocalName(element);

    // parsers 是通过 handler 调用 init 方法初始化的
    BeanDefinitionParser parser = (BeanDefinitionParser)this.parsers.get(localName);
    if (parser == null) {
        parserContext.getReaderContext().fatal("Cannot locate BeanDefinitionParser for element [" + localName + "]", element);
    }

    return parser;
}
```

### 步骤四，`Bean` 子流程
```java
// AbstractApplicationContext 类
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    if (beanFactory.containsBean("conversionService") && beanFactory.isTypeMatch("conversionService", ConversionService.class)) {
        beanFactory.setConversionService((ConversionService)beanFactory.getBean("conversionService", ConversionService.class));
    }

    if (!beanFactory.hasEmbeddedValueResolver()) {
        beanFactory.addEmbeddedValueResolver((strVal) -> {
            return this.getEnvironment().resolvePlaceholders(strVal);
        });
    }

    String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);

    for(int i = 0; i < weaverAwareNames.length; ++i) {
        String weaverAwareName = weaverAwareNames[i];
        this.getBean(weaverAwareName);
    }

    beanFactory.setTempClassLoader((ClassLoader)null);
    beanFactory.freezeConfiguration();
    beanFactory.preInstantiateSingletons();
}
```
```java
// DefaultListableBeanFactory 类
public void preInstantiateSingletons() throws BeansException {
    if (logger.isTraceEnabled()) {
        logger.trace("Pre-instantiating singletons in " + this);
    }

    // Iterate over a copy to allow for init methods which in turn register new bean definitions.
    // While this may not be part of the regular factory bootstrap, it does otherwise work fine.
    List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

    // Trigger initialization of all non-lazy singleton beans...
    for (String beanName : beanNames) {
        RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
        if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
            if (isFactoryBean(beanName)) {
                Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
                if (bean instanceof FactoryBean) {
                    final FactoryBean<?> factory = (FactoryBean<?>) bean;
                    boolean isEagerInit;
                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                        isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
                                        ((SmartFactoryBean<?>) factory)::isEagerInit,
                                getAccessControlContext());
                    }
                    else {
                        isEagerInit = (factory instanceof SmartFactoryBean &&
                                ((SmartFactoryBean<?>) factory).isEagerInit());
                    }
                    if (isEagerInit) {
                        getBean(beanName);
                    }
                }
            }
            else {
                getBean(beanName);
            }
        }
    }

    // Trigger post-initialization callback for all applicable beans...
    for (String beanName : beanNames) {
        Object singletonInstance = getSingleton(beanName);
        if (singletonInstance instanceof SmartInitializingSingleton) {
            final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                    smartSingleton.afterSingletonsInstantiated();
                    return null;
                }, getAccessControlContext());
            }
            else {
                smartSingleton.afterSingletonsInstantiated();
            }
        }
    }
}
```
```java
// AbstractBeanFactory 类
public Object getBean(String name) throws BeansException {
    return this.doGetBean(name, (Class)null, (Object[])null, false);
}

protected <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly) throws BeansException {
    final String beanName = transformedBeanName(name);
    Object bean;

    // Eagerly check singleton cache for manually registered singletons.
    Object sharedInstance = getSingleton(beanName);
    if (sharedInstance != null && args == null) {
        if (logger.isTraceEnabled()) {
            if (isSingletonCurrentlyInCreation(beanName)) {
                logger.trace("Returning eagerly cached instance of singleton bean '" + beanName +
                        "' that is not fully initialized yet - a consequence of a circular reference");
            }
            else {
                logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
            }
        }
        bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
    }

    else {
        // Fail if we're already creating this bean instance:
        // We're assumably within a circular reference.
        if (isPrototypeCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }

        // Check if bean definition exists in this factory.
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            // Not found -> check parent.
            String nameToLookup = originalBeanName(name);
            if (parentBeanFactory instanceof AbstractBeanFactory) {
                return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
                        nameToLookup, requiredType, args, typeCheckOnly);
            }
            else if (args != null) {
                // Delegation to parent with explicit args.
                return (T) parentBeanFactory.getBean(nameToLookup, args);
            }
            else if (requiredType != null) {
                // No args -> delegate to standard getBean method.
                return parentBeanFactory.getBean(nameToLookup, requiredType);
            }
            else {
                return (T) parentBeanFactory.getBean(nameToLookup);
            }
        }

        if (!typeCheckOnly) {
            markBeanAsCreated(beanName);
        }

        try {
            final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            checkMergedBeanDefinition(mbd, beanName, args);

            // Guarantee initialization of beans that the current bean depends on.
            String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                for (String dep : dependsOn) {
                    if (isDependent(beanName, dep)) {
                        throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                    }
                    registerDependentBean(dep, beanName);
                    try {
                        getBean(dep);
                    }
                    catch (NoSuchBeanDefinitionException ex) {
                        throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
                    }
                }
            }

            // Create bean instance.
            if (mbd.isSingleton()) {
                sharedInstance = getSingleton(beanName, () -> {
                    try {
                        return createBean(beanName, mbd, args);
                    }
                    catch (BeansException ex) {
                        // Explicitly remove instance from singleton cache: It might have been put there
                        // eagerly by the creation process, to allow for circular reference resolution.
                        // Also remove any beans that received a temporary reference to the bean.
                        destroySingleton(beanName);
                        throw ex;
                    }
                });
                bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
            }

            else if (mbd.isPrototype()) {
                // It's a prototype -> create a new instance.
                Object prototypeInstance = null;
                try {
                    beforePrototypeCreation(beanName);
                    prototypeInstance = createBean(beanName, mbd, args);
                }
                finally {
                    afterPrototypeCreation(beanName);
                }
                bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
            }

            else {
                String scopeName = mbd.getScope();
                final Scope scope = this.scopes.get(scopeName);
                if (scope == null) {
                    throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
                }
                try {
                    Object scopedInstance = scope.get(beanName, () -> {
                        beforePrototypeCreation(beanName);
                        try {
                            return createBean(beanName, mbd, args);
                        }
                        finally {
                            afterPrototypeCreation(beanName);
                        }
                    });
                    bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                }
                catch (IllegalStateException ex) {
                    throw new BeanCreationException(beanName,
                            "Scope '" + scopeName + "' is not active for the current thread; consider " +
                            "defining a scoped proxy for this bean if you intend to refer to it from a singleton",
                            ex);
                }
            }
        }
        catch (BeansException ex) {
            cleanupAfterBeanCreationFailure(beanName);
            throw ex;
        }
    }

    // Check if required type matches the type of the actual bean instance.
    if (requiredType != null && !requiredType.isInstance(bean)) {
        try {
            T convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
            if (convertedBean == null) {
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
            return convertedBean;
        }
        catch (TypeMismatchException ex) {
            if (logger.isTraceEnabled()) {
                logger.trace("Failed to convert bean '" + name + "' to required type '" +
                        ClassUtils.getQualifiedName(requiredType) + "'", ex);
            }
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        }
    }
    return (T) bean;
}
```
```java
// DefaultSingletonBeanRegistry 类
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    Assert.notNull(beanName, "Bean name must not be null");
    synchronized (this.singletonObjects) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            if (this.singletonsCurrentlyInDestruction) {
                throw new BeanCreationNotAllowedException(beanName,
                        "Singleton bean creation not allowed while singletons of this factory are in destruction " +
                        "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
            }
            beforeSingletonCreation(beanName);
            boolean newSingleton = false;
            boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
            if (recordSuppressedExceptions) {
                this.suppressedExceptions = new LinkedHashSet<>();
            }
            try {
                // 会调用到 createBean 方法
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            catch (IllegalStateException ex) {
                // Has the singleton object implicitly appeared in the meantime ->
                // if yes, proceed with it since the exception indicates that state.
                singletonObject = this.singletonObjects.get(beanName);
                if (singletonObject == null) {
                    throw ex;
                }
            }
            catch (BeanCreationException ex) {
                if (recordSuppressedExceptions) {
                    for (Exception suppressedException : this.suppressedExceptions) {
                        ex.addRelatedCause(suppressedException);
                    }
                }
                throw ex;
            }
            finally {
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = null;
                }
                afterSingletonCreation(beanName);
            }
            if (newSingleton) {
                addSingleton(beanName, singletonObject);
            }
        }
        return singletonObject;
    }
}
```
```java
// AbstractAutowireCapableBeanFactory 类

// 创建 bean 实例，填充，应用后处理器
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
    if (logger.isTraceEnabled()) {
        logger.trace("Creating instance of bean '" + beanName + "'");
    }
    RootBeanDefinition mbdToUse = mbd;

    // Make sure bean class is actually resolved at this point, and
    // clone the bean definition in case of a dynamically resolved Class
    // which cannot be stored in the shared merged bean definition.
    Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
    if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
        mbdToUse = new RootBeanDefinition(mbd);
        mbdToUse.setBeanClass(resolvedClass);
    }

    // Prepare method overrides.
    try {
        mbdToUse.prepareMethodOverrides();
    }
    catch (BeanDefinitionValidationException ex) {
        throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
                beanName, "Validation of method overrides failed", ex);
    }

    try {
        // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
        // 工厂后处理器，实例化 bean 之前调用
        // applyBeanPostProcessorsBeforeInstantiation
        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }
    }
    catch (Throwable ex) {
        throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
                "BeanPostProcessor before instantiation of bean failed", ex);
    }

    try {
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        if (logger.isTraceEnabled()) {
            logger.trace("Finished creating instance of bean '" + beanName + "'");
        }
        return beanInstance;
    }
    catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
        // A previously detected exception with proper bean creation context already,
        // or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
        throw ex;
    }
    catch (Throwable ex) {
        throw new BeanCreationException(
                mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
    }
}

protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args) throws BeanCreationException {
    // Instantiate the bean.
    BeanWrapper instanceWrapper = null;
    if (mbd.isSingleton()) {
        instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
    }

    if (instanceWrapper == null) {
        // 创建 Bean 实例，但未设置属性
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    final Object bean = instanceWrapper.getWrappedInstance();
    Class<?> beanType = instanceWrapper.getWrappedClass();
    if (beanType != NullBean.class) {
        mbd.resolvedTargetType = beanType;
    }

    // Allow post-processors to modify the merged bean definition.
    synchronized (mbd.postProcessingLock) {
        if (!mbd.postProcessed) {
            try {
                applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
            }
            catch (Throwable ex) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                        "Post-processing of merged bean definition failed", ex);
            }
            mbd.postProcessed = true;
        }
    }

    // Eagerly cache singletons to be able to resolve circular references
    // even when triggered by lifecycle interfaces like BeanFactoryAware.
    boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
            isSingletonCurrentlyInCreation(beanName));
    if (earlySingletonExposure) {
        if (logger.isTraceEnabled()) {
            logger.trace("Eagerly caching bean '" + beanName +
                    "' to allow for resolving potential circular references");
        }
        addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
    }

    // Initialize the bean instance.
    Object exposedObject = bean;
    try {
        // 填充属性，调用 init-method，并应用 BeanPostProcessor
        // postProcessAfterInstantiation
        populateBean(beanName, mbd, instanceWrapper);
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    }
    catch (Throwable ex) {
        if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
            throw (BeanCreationException) ex;
        }
        else {
            throw new BeanCreationException(
                    mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
        }
    }

    if (earlySingletonExposure) {
        Object earlySingletonReference = getSingleton(beanName, false);
        if (earlySingletonReference != null) {
            if (exposedObject == bean) {
                exposedObject = earlySingletonReference;
            }
            else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
                String[] dependentBeans = getDependentBeans(beanName);
                Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
                for (String dependentBean : dependentBeans) {
                    if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                        actualDependentBeans.add(dependentBean);
                    }
                }
                if (!actualDependentBeans.isEmpty()) {
                    throw new BeanCurrentlyInCreationException(beanName,
                            "Bean with name '" + beanName + "' has been injected into other beans [" +
                            StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
                            "] in its raw version as part of a circular reference, but has eventually been " +
                            "wrapped. This means that said other beans do not use the final version of the " +
                            "bean. This is often the result of over-eager type matching - consider using " +
                            "'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
                }
            }
        }
    }

    // Register bean as disposable.
    try {
        registerDisposableBeanIfNecessary(beanName, bean, mbd);
    }
    catch (BeanDefinitionValidationException ex) {
        throw new BeanCreationException(
                mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
    }

    return exposedObject;
}

protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    // Make sure bean class is actually resolved at this point.
    Class<?> beanClass = resolveBeanClass(mbd, beanName);

    if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
        throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
    }

    Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
    if (instanceSupplier != null) {
        return obtainFromSupplier(instanceSupplier, beanName);
    }

    if (mbd.getFactoryMethodName() != null) {
        return instantiateUsingFactoryMethod(beanName, mbd, args);
    }

    // Shortcut when re-creating the same bean...
    boolean resolved = false;
    boolean autowireNecessary = false;
    if (args == null) {
        synchronized (mbd.constructorArgumentLock) {
            if (mbd.resolvedConstructorOrFactoryMethod != null) {
                resolved = true;
                autowireNecessary = mbd.constructorArgumentsResolved;
            }
        }
    }
    if (resolved) {
        if (autowireNecessary) {
            return autowireConstructor(beanName, mbd, null, null);
        }
        else {
            return instantiateBean(beanName, mbd);
        }
    }

    // Candidate constructors for autowiring?
    Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
    if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
            mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
        return autowireConstructor(beanName, mbd, ctors, args);
    }

    // Preferred constructors for default construction?
    ctors = mbd.getPreferredConstructors();
    if (ctors != null) {
        return autowireConstructor(beanName, mbd, ctors, null);
    }

    // No special handling: simply use no-arg constructor.
    return instantiateBean(beanName, mbd);
}

// AbstractAutowireCapableBeanFactory 类
// 使用默认构造器实例化 bean
protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
    try {
        Object beanInstance;
        final BeanFactory parent = this;
        if (System.getSecurityManager() != null) {
            beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>) () ->
                    getInstantiationStrategy().instantiate(mbd, beanName, parent),
                    getAccessControlContext());
        }
        else {
            // 使用相应的实例化策略实例化 bean
            beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
        }

        // BeanWrapperImpl 具有三重身份
        // 1. Bean 包裹器
        // 2. 属性访问器
        // 3. 属性编辑器注册表
        BeanWrapper bw = new BeanWrapperImpl(beanInstance);

        // 注册自定义的 editors
        initBeanWrapper(bw);
        return bw;
    }
    catch (Throwable ex) {
        throw new BeanCreationException(
                mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
    }
}

// SimpleInstantiationStrategy 类
public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
    // Don't override the class with CGLIB if no overrides.
    if (!bd.hasMethodOverrides()) {
        Constructor<?> constructorToUse;
        synchronized (bd.constructorArgumentLock) {
            constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;
            if (constructorToUse == null) {
                final Class<?> clazz = bd.getBeanClass();
                if (clazz.isInterface()) {
                    throw new BeanInstantiationException(clazz, "Specified class is an interface");
                }
                try {
                    if (System.getSecurityManager() != null) {
                        constructorToUse = AccessController.doPrivileged(
                                (PrivilegedExceptionAction<Constructor<?>>) clazz::getDeclaredConstructor);
                    }
                    else {
                        constructorToUse = clazz.getDeclaredConstructor();
                    }
                    bd.resolvedConstructorOrFactoryMethod = constructorToUse;
                }
                catch (Throwable ex) {
                    throw new BeanInstantiationException(clazz, "No default constructor found", ex);
                }
            }
        }

        // 使用 BeanUtils 根据构造器实例化 bean
        return BeanUtils.instantiateClass(constructorToUse);
    }
    else {
        // Must generate CGLIB subclass.
        return instantiateWithMethodInjection(bd, beanName, owner);
    }
}

protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
    if (System.getSecurityManager() != null) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            invokeAwareMethods(beanName, bean);
            return null;
        }, getAccessControlContext());
    }
    else {
        invokeAwareMethods(beanName, bean);
    }

    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }

    try {
        invokeInitMethods(beanName, wrappedBean, mbd);
    }
    catch (Throwable ex) {
        throw new BeanCreationException(
                (mbd != null ? mbd.getResourceDescription() : null),
                beanName, "Invocation of init method failed", ex);
    }
    if (mbd == null || !mbd.isSynthetic()) {
        // 应用 BeanPostProcessor
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    return wrappedBean;
}
```