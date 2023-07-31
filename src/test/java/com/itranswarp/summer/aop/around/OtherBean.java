package com.itranswarp.summer.aop.around;

import com.gaoda.annotation.Autowired;
import com.gaoda.annotation.Component;
import com.gaoda.annotation.Order;

@Order(0)
@Component
public class OtherBean {

    public OriginBean origin;

    public OtherBean(@Autowired OriginBean origin) {
        this.origin = origin;
    }
}