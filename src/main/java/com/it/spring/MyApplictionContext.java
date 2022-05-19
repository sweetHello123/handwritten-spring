package com.it.spring;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: china wu
 * @Description: spring容器
 * @Date: 2022/5/18 23:12
 */
public class MyApplictionContext {

    private Class clazz;

    /**
     * 单例池
     */
    private ConcurrentHashMap<String, Object> singletonObject = new ConcurrentHashMap<>();

    /**
     * beanDefinition池 - 用于保存bean定义对象
     */
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public MyApplictionContext(Class clazz) {
        this.clazz = clazz;
        // 解析配置类
        scan(clazz);
        // 单例对象需要容器启动时全部生成好放入单例池
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                Object bean = createBean(beanDefinition);
                singletonObject.put(beanName, bean);
            }
        }

    }

    /**
     * 根据beanDefinition创建bean对象
     *
     * @param beanDefinition
     * @return
     */
    public Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        Object instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return instance;
    }

    private void scan(Class clazz) {
        // 1.通过获取组件扫描注解，取到扫描路径
        ComponentScan componentScan = (ComponentScan) clazz.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScan.value();

        // 2.通过app类加载器获取该路径下的资源
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(path.replace('.', '/'));
        if (resource != null) {
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String filePath = f.getAbsolutePath();
                        // 解析以.class结尾的文件
                        if (filePath.endsWith(".class")) {
                            filePath = filePath.substring(filePath.lastIndexOf("\\"), filePath.lastIndexOf(".class"));
                            // 获取资源类限定名 - 包名.类名
                            String className = path + filePath.replace("\\", ".");
                            try {
                                // 加载资源类
                                Class<?> aClass = classLoader.loadClass(className);
                                // 判定该类是否加了Component注解，即扫描组件过程
                                if (aClass.isAnnotationPresent(Component.class)) {
                                    // 获取该类标注的Component注解
                                    Component component = aClass.getDeclaredAnnotation(Component.class);
                                    // 获取bean名称
                                    String beanName = component.value();

                                    // 创建bean的定义对象
                                    BeanDefinition beanDefinition = new BeanDefinition();
                                    // 将该类class设置给beanDefinition的clazz属性
                                    beanDefinition.setClazz(aClass);
                                    // 判断该类是否加了scope注解
                                    if (aClass.isAnnotationPresent(Scope.class)) {
                                        // 获取该类标注的Scope注解
                                        Scope scope = aClass.getDeclaredAnnotation(Scope.class);
                                        // 给bean定义对象scope属性赋值
                                        beanDefinition.setScope(scope.value());
                                    } else {
                                        // 没有加scope注解默认为单例
                                        beanDefinition.setScope("singleton");
                                    }
                                    // 加入beanDefinition池
                                    beanDefinitionMap.put(beanName, beanDefinition);
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据bean名称获取bean对象
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        // 判断bean是否生成了beanDefinition对象
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                // 单例对象直接从单例池取
                return singletonObject.get(beanName);
            } else {
                return createBean(beanDefinition);
            }
        } else {
            throw new NullPointerException();
        }
    }

}
