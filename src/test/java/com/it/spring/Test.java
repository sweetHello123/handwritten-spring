package com.it.spring;

import com.it.me.AppConfig;

/**
 * @Author: china wu
 * @Description:
 * @Date: 2022/5/19 8:31
 */
public class Test {
    public static void main(String[] args) {
        MyApplictionContext context = new MyApplictionContext(AppConfig.class);
        System.out.println("单例userService:");
        System.out.println(context.getBean("userService"));
        System.out.println(context.getBean("userService"));
        System.out.println(context.getBean("userService"));
        System.out.println("原型studentService:");
        System.out.println(context.getBean("studentService"));
        System.out.println(context.getBean("studentService"));
        System.out.println(context.getBean("studentService"));

    }
}
