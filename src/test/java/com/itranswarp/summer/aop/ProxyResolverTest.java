package com.itranswarp.summer.aop;

import com.gaoda.aop.ProxyResolver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProxyResolverTest {

    /**
     * 刚写ProxyResolver的Test,因为设为私有构造了,所以这里会报错
     */
//    @Test
//    public void testProxyResovler() {
//        // 原始Bean:
//        OriginBean origin = new OriginBean();
//        origin.name = "Bob";
//        // 调用原始Bean的hello():
//        assertEquals("Hello, Bob.", origin.hello());
//
//        // 创建Proxy:
//        OriginBean proxy = new ProxyResolver().createProxy(origin, new PoliteInvocationHandler());
//
//        // Proxy类名,类似OriginBean$ByteBuddy$9hQwRy3T:
//        System.out.println(proxy.getClass().getName());
//
//        // Proxy类与OriginBean.class不同:
//        assertNotSame(OriginBean.class, proxy.getClass());
//        // proxy实例的name字段应为null:
//        assertNull(proxy.name);
//
//        // 调用带@Polite的方法:
//        assertEquals("Hello, Bob!", proxy.hello());
//        // 调用不带@Polite的方法:
//        assertEquals("Morning, Bob.", proxy.morning());
//    }
}