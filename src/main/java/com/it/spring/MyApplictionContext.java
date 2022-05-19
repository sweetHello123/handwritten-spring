package com.it.spring;

import java.io.File;
import java.net.URL;

/**
 * @Author: china wu
 * @Description:
 * @Date: 2022/5/18 23:12
 */
public class MyApplictionContext {

    private Class clazz;

    public MyApplictionContext(Class clazz) {
        this.clazz = clazz;
        // 解析配置类
        // 1.通过获取组件扫描注解，取到扫描路径
        ComponentScan annotation = (ComponentScan) clazz.getDeclaredAnnotation(ComponentScan.class);
        String path = annotation.value();
        System.out.println(path);

        // 2.通过app类加载器获取该路径下的资源
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("com/it/me/service");
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    String filePath = f.getAbsolutePath();
                    if (filePath.endsWith(".class")) {
                        filePath = filePath.substring(filePath.lastIndexOf("\\"), filePath.lastIndexOf(".class"));
                        // 获取资源类限定名
                        String className = path + filePath.replace("\\", ".");
                        System.out.println(className);
                        try {
                            // 加载资源类
                            Class<?> aClass = classLoader.loadClass(className);
                            // 判定该类是否加了Component注解，即扫描组件过程
                            if (aClass.isAnnotationPresent(Component.class)) {

                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        }

    }

    public Object getBean(String beanName) {

        return null;
    }

}
