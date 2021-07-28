此项目为手写Spring5 30个类

看此项目,建议全程搭配此文档食用,效果更佳

@Target注解:
说明 当前注解类所修饰的对象范围 ElementType--定义范围
// 定义这个注解的生命周期
@Retention(RetentionPolicy.RUNTIME)
// 在生成java文档时 把这个注解标注出来
@Documented

spring-beans 和 spring-core 模块是 Spring 框架的核心模块，包含了控制反转（Inversion of
Control, IOC）和依赖注入（Dependency Injection, DI）
BeanFactory 接口是 Spring 框架中的核心接口，它是工厂模式的具体实现


spring-context 模块构架于核心模块之上，他扩展了 BeanFactory，为她添加了 Bean 生命周期
控制、框架事件体系以及资源加载透明化等功能




