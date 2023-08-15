package com.itranswarp.summer.context;

import com.gaoda.context.AnnotationConfigApplicationContext;
import com.gaoda.context.BeanDefinition;
import com.gaoda.io.PropertyResolver;
import com.itranswarp.imported.LocalDateConfiguration;
import com.itranswarp.imported.ZonedDateConfiguration;
import com.itranswarp.scan.ScanApplication;
import com.itranswarp.scan.convert.ValueConverterBean;
import com.itranswarp.scan.custom.annotation.CustomAnnotationBean;
import com.itranswarp.scan.destroy.AnnotationDestroyBean;
import com.itranswarp.scan.destroy.SpecifyDestroyBean;
import com.itranswarp.scan.init.AnnotationInitBean;
import com.itranswarp.scan.init.SpecifyInitBean;
import com.itranswarp.scan.nested.OuterBean;
import com.itranswarp.scan.primary.DogBean;
import com.itranswarp.scan.primary.PersonBean;
import com.itranswarp.scan.primary.StudentBean;
import com.itranswarp.scan.primary.TeacherBean;
import com.itranswarp.scan.proxy.InjectProxyOnConstructorBean;
import com.itranswarp.scan.proxy.InjectProxyOnPropertyBean;
import com.itranswarp.scan.proxy.OriginBean;
import com.itranswarp.scan.proxy.SecondProxyBean;
import com.itranswarp.scan.sub1.Sub1Bean;
import com.itranswarp.scan.sub1.sub2.Sub2Bean;
import com.itranswarp.scan.sub1.sub2.sub3.Sub3Bean;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationConfigApplicationContextTest {

    @Test
    public void testAnnotationConfigApplicationContext() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        // @CustomAnnotation:
        assertNotNull(ctx.findBeanDefinition(CustomAnnotationBean.class));
        assertNotNull(ctx.findBeanDefinition("customAnnotation"));

        // @Import():
        assertNotNull(ctx.findBeanDefinition(LocalDateConfiguration.class));
        assertNotNull(ctx.findBeanDefinition("startLocalDate"));
        assertNotNull(ctx.findBeanDefinition("startLocalDateTime"));
        assertNotNull(ctx.findBeanDefinition(ZonedDateConfiguration.class));
        assertNotNull(ctx.findBeanDefinition("startZonedDateTime"));
        // nested:
        assertNotNull(ctx.findBeanDefinition(OuterBean.class));
        assertNotNull(ctx.findBeanDefinition(OuterBean.NestedBean.class));

        BeanDefinition studentDef = ctx.findBeanDefinition(StudentBean.class);
        BeanDefinition teacherDef = ctx.findBeanDefinition(TeacherBean.class);
        // 2 PersonBean:
        List<BeanDefinition> defs = ctx.findBeanDefinitions(PersonBean.class);
        assertSame(studentDef, defs.get(0));
        assertSame(teacherDef, defs.get(1));
        // 1 @Primary PersonBean:
        BeanDefinition personPrimaryDef = ctx.findBeanDefinition(PersonBean.class);
        assertSame(teacherDef, personPrimaryDef);
    }

    @Test
    public void testCustomAnnotation() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        assertNotNull(ctx.getBean(CustomAnnotationBean.class));
        assertNotNull(ctx.getBean("customAnnotation"));
    }


    @Test
    public void testInitMethod() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        // test @PostConstruct:
        var bean1 = ctx.getBean(AnnotationInitBean.class);
        var bean2 = ctx.getBean(SpecifyInitBean.class);
        assertEquals("Scan App / v1.0", bean1.appName);
        assertEquals("Scan App / v1.0", bean2.appName);
    }


    @Test
    public void testImport() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        assertNotNull(ctx.getBean(LocalDateConfiguration.class));
        assertNotNull(ctx.getBean("startLocalDate"));
        assertNotNull(ctx.getBean("startLocalDateTime"));
        assertNotNull(ctx.getBean(ZonedDateConfiguration.class));
        assertNotNull(ctx.getBean("startZonedDateTime"));
    }


    @Test
    public void testDestroyMethod() {
        AnnotationDestroyBean bean1 = null;
        SpecifyDestroyBean bean2 = null;
        try (var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver())) {
            // test @PreDestroy:
            bean1 = ctx.getBean(AnnotationDestroyBean.class);
            bean2 = ctx.getBean(SpecifyDestroyBean.class);
            assertEquals("Scan App", bean1.appTitle);
            assertEquals("Scan App", bean2.appTitle);
        }
        assertNull(bean1.appTitle);
        assertNull(bean2.appTitle);
    }



    @Test
    public void testConverter() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        var bean = ctx.getBean(ValueConverterBean.class);

        assertNotNull(bean.injectedBoolean);
        assertTrue(bean.injectedBooleanPrimitive);
        assertTrue(bean.injectedBoolean);

        assertNotNull(bean.injectedByte);
        assertEquals((byte) 123, bean.injectedByte);
        assertEquals((byte) 123, bean.injectedBytePrimitive);

        assertNotNull(bean.injectedShort);
        assertEquals((short) 12345, bean.injectedShort);
        assertEquals((short) 12345, bean.injectedShortPrimitive);

        assertNotNull(bean.injectedInteger);
        assertEquals(1234567, bean.injectedInteger);
        assertEquals(1234567, bean.injectedIntPrimitive);

        assertNotNull(bean.injectedLong);
        assertEquals(123456789_000L, bean.injectedLong);
        assertEquals(123456789_000L, bean.injectedLongPrimitive);

        assertNotNull(bean.injectedFloat);
        assertEquals(12345.6789F, bean.injectedFloat, 0.0001F);
        assertEquals(12345.6789F, bean.injectedFloatPrimitive, 0.0001F);

        assertNotNull(bean.injectedDouble);
        assertEquals(123456789.87654321, bean.injectedDouble, 0.0000001);
        assertEquals(123456789.87654321, bean.injectedDoublePrimitive, 0.0000001);

        assertEquals(LocalDate.parse("2023-03-29"), bean.injectedLocalDate);
        assertEquals(LocalTime.parse("20:45:01"), bean.injectedLocalTime);
        assertEquals(LocalDateTime.parse("2023-03-29T20:45:01"), bean.injectedLocalDateTime);
        assertEquals(ZonedDateTime.parse("2023-03-29T20:45:01+08:00[Asia/Shanghai]"), bean.injectedZonedDateTime);
        assertEquals(Duration.parse("P2DT3H4M"), bean.injectedDuration);
        assertEquals(ZoneId.of("Asia/Shanghai"), bean.injectedZoneId);
    }



    @Test
    public void testNested() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        ctx.getBean(OuterBean.class);
        ctx.getBean(OuterBean.NestedBean.class);
    }

    @Test
    public void testPrimary() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        var person = ctx.getBean(PersonBean.class);
        assertEquals(TeacherBean.class, person.getClass());
        var dog = ctx.getBean(DogBean.class);
        assertEquals("Husky", dog.type);
    }

    @Test
    public void testSub() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        ctx.getBean(Sub1Bean.class);
        ctx.getBean(Sub2Bean.class);
        ctx.getBean(Sub3Bean.class);
    }



    @Test
    public void testProxy() {
        var ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        // test proxy:
        // 获取OriginBean的实例,此处获取的应该是SendProxyBeanProxy:
        OriginBean proxy = ctx.getBean(OriginBean.class);
        assertSame(SecondProxyBean.class, proxy.getClass());
        // 调用proxy的getName()会最终调用原始Bean的getName(),从而返回正确的值:
        assertEquals("Scan App", proxy.getName());
        assertEquals("v1.0", proxy.getVersion());
        // 但proxy的name和version字段并没有被注入:
        assertNull(proxy.name);
        assertNull(proxy.version);

        // 获取InjectProxyOnConstructorBean实例:
        var inject1 = ctx.getBean(InjectProxyOnPropertyBean.class);
        var inject2 = ctx.getBean(InjectProxyOnConstructorBean.class);
        // 注入的OriginBean应该为Proxy，而且和前面返回的proxy是同一实例:
        assertSame(proxy, inject1.injected);
        assertSame(proxy, inject2.injected);
    }

    /**
     * 从上面的测试代码我们也能看出，对于使用Proxy模式的Bean来说，正常的方法调用对用户是透明的，
     * 但是，直接访问Bean注入的字段，如果获取的是Proxy，则字段全部为null，因为注入并没有发生在Proxy，而是原始Bean。
     * 这也是为什么当我们需要访问某个注入的Bean时，总是调用方法而不是直接访问字段：
     * eg :
     *  @Component
     * public class MailService {
     *     @Autowired
     *     UserService userService;
     *
     *     public String sendMail() {
     *         // 错误:不要直接访问UserService的字段,因为如果UserService被代理,则返回null:
     *         ZoneId zoneId = userService.zoneId;
     *         // 正确:通过方法访问UserService的字段,无论是否被代理,返回值均是正确的:
     *         ZoneId zoneId = userService.getZoneId();
     *         ...
     *     }
     * }
     */


    PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        ps.put("app.title", "Scan App");
        ps.put("app.version", "v1.0");
        ps.put("jdbc.url", "jdbc:hsqldb:file:testdb.tmp");
        ps.put("jdbc.username", "sa");
        ps.put("jdbc.password", "");
        ps.put("convert.boolean", "true");
        ps.put("convert.byte", "123");
        ps.put("convert.short", "12345");
        ps.put("convert.integer", "1234567");
        ps.put("convert.long", "123456789000");
        ps.put("convert.float", "12345.6789");
        ps.put("convert.double", "123456789.87654321");
        ps.put("convert.localdate", "2023-03-29");
        ps.put("convert.localtime", "20:45:01");
        ps.put("convert.localdatetime", "2023-03-29T20:45:01");
        ps.put("convert.zoneddatetime", "2023-03-29T20:45:01+08:00[Asia/Shanghai]");
        ps.put("convert.duration", "P2DT3H4M");
        ps.put("convert.zoneid", "Asia/Shanghai");
        var pr = new PropertyResolver(ps);
        return pr;
    }






}