package com.itranswarp.summer.aop.around;

import com.gaoda.annotation.Bean;
import com.gaoda.annotation.ComponentScan;
import com.gaoda.annotation.Configuration;
import com.gaoda.aop.AroundProxyBeanPostProcessor;

@Configuration
@ComponentScan
public class AroundApplication {

    @Bean
    AroundProxyBeanPostProcessor createAroundProxyBeanPostProcessor() {
        return new AroundProxyBeanPostProcessor();
    }
}