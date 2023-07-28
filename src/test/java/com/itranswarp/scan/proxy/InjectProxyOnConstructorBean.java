package com.itranswarp.scan.proxy;

import com.gaoda.annotation.Autowired;
import com.gaoda.annotation.Component;

@Component
public class InjectProxyOnConstructorBean {

    public final OriginBean injected;

    public InjectProxyOnConstructorBean(@Autowired OriginBean injected) {
        this.injected = injected;
    }
}