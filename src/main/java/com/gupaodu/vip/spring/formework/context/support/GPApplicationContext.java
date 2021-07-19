package com.gupaodu.vip.spring.formework.context.support;

import com.gupaodu.vip.spring.formework.annotation.GPAutowired;
import com.gupaodu.vip.spring.formework.annotation.GPController;
import com.gupaodu.vip.spring.formework.annotation.GPService;
import com.gupaodu.vip.spring.formework.beans.GPBeanWrapper;
import com.gupaodu.vip.spring.formework.beans.config.GPBeanDefinition;
import com.gupaodu.vip.spring.formework.beans.config.GPBeanPostProcessor;
import com.gupaodu.vip.spring.formework.beans.support.GPBeanDefinitionReader;
import com.gupaodu.vip.spring.formework.beans.support.GPDefaultListableBeanFactory;
import com.gupaodu.vip.spring.formework.core.GPBeanFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @创建人 dw
 * @创建时间 2021/5/17
 * @描述
 */
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {


    private String[] configLoactions;

    private GPBeanDefinitionReader reader;

    // 单例的IOC容器缓存 用来保证注册式的容器
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    // 通用的IOC容器,用来存储所有被代理过的对象
    private Map<String, GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, GPBeanWrapper>();

    /**
     * @param configLocations
     */
    public GPApplicationContext(String... configLocations) {
        this.configLoactions = configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // 1.定位配置文件
        reader = new GPBeanDefinitionReader((this.configLoactions));
        // 2. 加载配置文件,扫描相关的类,把他们封装成BeanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3. 注册,吧配置信息放到容器里面(IOC容器)
        doRegisterBeanDefinition(beanDefinitions);
        // 4. 把不是延时加载的类提前初始化
        doAutowrited();
    }

    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) throws Exception {
        for (GPBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("这个" + beanDefinition.getBeanClassName() + "不存在");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        // 到这里为止,容器初始化完毕
    }

    private void doAutowrited() {
        for (Map.Entry<String, GPBeanDefinition> beanDefinitionEntry :
                super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 依赖注入 从这里开始,读取BeanDefinition中的信息
    // 然后通过反射机制创建一个实体类,返回
    // Spring的做法是 不会把最原始的对象放出去,会用一个BeanWrapper 来进行一次包装
    // 装饰者模式 1.保留原来的OOP关系,需要对它进行扩展,增强(为了以后的AOP打基础)
    public Object getBean(String beanName) throws Exception {
        // 根据Bean名字拿到Bean
        GPBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        // 生成通知事件
        GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();
        Object instance = instantiateBean(beanDefinition);
        // 如果为null 说明没有 直接返回
        if (null == instance) {
            return null;
        }
        // 在实例初始化之前调用一次
        beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
        // 把原生对象 放到beanWrapper的wrappedInstance里
        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);
        // 存进IOC容器里
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);
        // 在实例初始化之后调用一次
        beanPostProcessor.postProcessAfterInitialization(instance, beanName);
        populateBean(beanName, instance);
        //  通过这样的调用 给自己留下操作空间
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();
        // 判断是不是GPController,GPService这两个类是的话直接返回
        if (!(clazz.isAnnotationPresent(GPController.class)) || clazz.isAnnotationPresent(GPService.class)) {
            return;
        }
        // 拿到类中所有的字段,getField只能拿到公有的
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 判断是不是GPAutowired不是直接跳出去
            if (!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }
            // 根据class返回对应的对象
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 传一个beanDefinition 就返回一个实例bean
     *
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(GPBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        // 判断是否实例化过这个类对象
        try {
            if (this.factoryBeanObjectCache.containsKey(className)) {
                // 拿到这个对象
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                // 没有的话 通过反射new这个对象,然后存到IOC容器里面
                instance = Class.forName(className).getInterfaces();
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
            return instance;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }


    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

    public static void main(String[] args) {
        RandomAccessFile aFile = null;
        try {
            /**
             * r 以只读的方式打开文本，也就意味着不能用write来操作文件
             * rw	读操作和写操作都是允许的
             * rws	每当进行写操作，同步的刷新到磁盘，刷新内容和元数据
             * rwd	每当进行写操作，同步的刷新到磁盘，刷新内容
             */
            //  new一个随机读取文件对象 传入name 文件路径, mode 权限
            aFile = new RandomAccessFile("C:\\Users\\PC\\Desktop\\test.txt", "rw");
            // 获取这个对象的管道 channel
            FileChannel inChannel = aFile.getChannel();
            // 创建容量为48字节的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(48);
            // 管道读入缓冲区
            int bytesRead = inChannel.read(buf);
            // 循环 只要 不为 -1
            while (bytesRead != -1) {
                System.out.println("Read " + bytesRead);
                // 使缓冲区准备好读取
                buf.flip();
                // 判断下一个要写入的元素小于总的列数,就读]
                while (buf.hasRemaining()) {
                    System.out.println(buf.get());
                }
                // 清空整个缓冲区
                // 还有一个清空已读数据缓冲区方法
                //compact()方法将所有未读的数据拷贝到Buffer起始处。
                // 然后将position设到最后一个未读元素正后面。limit属性依然像clear()方法一样，设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。
                buf.clear();
                // 把缓冲区转为写
                bytesRead = inChannel.read(buf);
            }

            aFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
