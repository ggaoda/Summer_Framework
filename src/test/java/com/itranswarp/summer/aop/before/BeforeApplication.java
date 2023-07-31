package com.itranswarp.summer.aop.before;

import com.gaoda.annotation.Bean;
import com.gaoda.annotation.ComponentScan;
import com.gaoda.annotation.Configuration;
import com.gaoda.aop.AroundProxyBeanPostProcessor;

@Configuration
@ComponentScan
public class BeforeApplication {

    @Bean
    AroundProxyBeanPostProcessor createAroundProxyBeanPostProcessor() {
        return new AroundProxyBeanPostProcessor();
    }
}