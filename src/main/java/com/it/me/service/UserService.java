package com.it.me.service;

import com.it.spring.Autowired;
import com.it.spring.Component;

/**
 * @Author: china wu
 * @Description: 无scope注解的组件
 * @Date: 2022/5/19 8:27
 */
@Component("userService")
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);
    }
}
