package com.itranswarp.scan.proxy;

import com.gaoda.annotation.Autowired;
import com.gaoda.annotation.Component;

@Component
public class InjectProxyOnPropertyBean {

    @Autowired
    public OriginBean injected;
}