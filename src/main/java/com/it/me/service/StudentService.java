package com.it.me.service;

import com.it.spring.Component;
import com.it.spring.Scope;

/**
 * @Author: china wu
 * @Description: 添加scope注解的组件
 * @Date: 2022/5/19 21:47
 */
@Scope("prototype")
@Component("studentService")
public class StudentService {
}
