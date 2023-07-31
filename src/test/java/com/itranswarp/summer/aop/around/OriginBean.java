package com.itranswarp.summer.aop.around;

import com.gaoda.annotation.Around;
import com.gaoda.annotation.Component;
import com.gaoda.annotation.Value;

@Component
@Around("aroundInvocationHandler")
public class OriginBean {

    @Value("${customer.name}")
    public String name;

    @Polite
    public String hello() {
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}