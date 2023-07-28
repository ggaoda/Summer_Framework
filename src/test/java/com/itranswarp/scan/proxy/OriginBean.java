package com.itranswarp.scan.proxy;

import com.gaoda.annotation.Component;
import com.gaoda.annotation.Value;

@Component
public class OriginBean {


    @Value("${app.title}")
    public String name;

    public String version;

    @Value("${app.version}")
    public void setVersion(String version) {
        this.version = version;
    }


    public String getName() {
        return name;
    }

    public String getVersion() {
        return this.version;
    }
}