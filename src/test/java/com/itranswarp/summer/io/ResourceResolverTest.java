package com.itranswarp.summer.io;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import com.gaoda.io.ResourceResolver;
import org.junit.jupiter.api.Test;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sub.AnnoScan;

public class ResourceResolverTest {

    /**
     * 我们传入Resource到Class Name的映射，就可以扫描出Class Name：
     * ResourceResolver只负责扫描并列出所有文件，由客户端决定是找出.class文件，还是找出.properties文件。
     */
    @Test
    public void scanClass() {
        // 在pkg下扫描
        String pkg = "com.itranswarp.scan";
        // 定义一个扫描器
        ResourceResolver rr = new ResourceResolver(pkg);
        List<String> classes = rr.scan(res -> {
            String name = res.name(); // 资源名称"org/example/Hello.class"
            if (name.endsWith(".class")) { // 如果以.class结尾
                // 把"org/example/Hello.class"变为"org.example.Hello":
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            // 否则返回null表示不是有效的Class Name:
            return null;
        });
        Collections.sort(classes);
        System.out.println(classes);
        String[] listClasses = new String[] {
                // list of some scan classes:
                "com.itranswarp.scan.convert.ValueConverterBean", //
                "com.itranswarp.scan.destroy.AnnotationDestroyBean", //
                "com.itranswarp.scan.init.SpecifyInitConfiguration", //
                "com.itranswarp.scan.proxy.OriginBean", //
                "com.itranswarp.scan.proxy.FirstProxyBeanPostProcessor", //
                "com.itranswarp.scan.proxy.SecondProxyBeanPostProcessor", //
                "com.itranswarp.scan.nested.OuterBean", //
                "com.itranswarp.scan.nested.OuterBean$NestedBean", //
                "com.itranswarp.scan.sub1.Sub1Bean", //
                "com.itranswarp.scan.sub1.sub2.Sub2Bean", //
                "com.itranswarp.scan.sub1.sub2.sub3.Sub3Bean", //
        };
        for (String clazz : listClasses) {
            assertTrue(classes.contains(clazz));
        }
    }

    // 这样，ResourceResolver只负责扫描并列出所有文件，
    // 由客户端决定是找出.class文件，还是找出.properties文件。

    @Test
    public void scanJar() {
        // 扫描@PostConstruct这个注解所在包(jar)
        var pkg = PostConstruct.class.getPackageName();
        var rr = new ResourceResolver(pkg);
        List<String> classes = rr.scan(res -> {
            String name = res.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            return null;
        });
        // classes in jar:
        assertTrue(classes.contains(PostConstruct.class.getName()));
        assertTrue(classes.contains(PreDestroy.class.getName()));
        assertTrue(classes.contains(PermitAll.class.getName()));
        assertTrue(classes.contains(DataSourceDefinition.class.getName()));
        // jakarta.annotation.sub.AnnoScan is defined in classes:
        assertTrue(classes.contains(AnnoScan.class.getName()));
    }

    @Test
    public void scanTxt() {
        var pkg = "com.itranswarp.scan";
        var rr = new ResourceResolver(pkg);
        List<String> classes = rr.scan(res -> {
            String name = res.name();
            if (name.endsWith(".txt")) {
                return name.replace("\\", "/");
            }
            return null;
        });
        Collections.sort(classes);
        assertArrayEquals(new String[] {
                // txt files:
                "com/itranswarp/scan/sub1/sub1.txt", //
                "com/itranswarp/scan/sub1/sub2/sub2.txt", //
                "com/itranswarp/scan/sub1/sub2/sub3/sub3.txt", //
        }, classes.toArray(String[]::new));
    }
}
