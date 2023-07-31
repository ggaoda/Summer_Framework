package com.gaoda.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在ClassPath中扫描文件的代码是固定模式，可以在网上搜索获得，例如StackOverflow的这个回答。
 * 这里要注意的一点是，Java支持在jar包中搜索文件，
 * 所以，不但需要在普通目录中搜索，也需要在Classpath中列出的jar包中搜索，核心代码如下：
 *
 * A simple classpath scan works both in directory and jar:
 *
 * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection#58773038
 */
public class ResourceResolver {

    Logger logger = LoggerFactory.getLogger(getClass());

    String basePackage;

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * scan()方法传入了一个映射函数，我们传入Resource到Class Name的映射，就可以扫描出Class Name
     * 这样，ResourceResolver只负责扫描并列出所有文件，由客户端决定是找出.class文件，还是找出.properties文件
     * @param mapper
     * @return
     * @param <R>
     */
    public <R> List<R> scan(Function<Resource, R> mapper) {
        // 将 x.y.z 转化为 x/y/z
        String basePackagePath = this.basePackage.replace(".", "/");
        String path = basePackagePath;
        try {
            List<R> collector = new ArrayList<>();
            scan0(basePackagePath, path, collector, mapper);
            return collector;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取到URL列表,然后根据每个的类型(jar/file)进行对应的处理scanFile
     * @param basePackagePath
     * @param path
     * @param collector
     * @param mapper
     * @param <R>
     * @throws IOException
     * @throws URISyntaxException
     */
    <R> void scan0(String basePackagePath, String path, List<R> collector, Function<Resource, R> mapper) throws IOException, URISyntaxException {
        // 打印日志
        logger.atDebug().log("scan path: {}", path);
        // 通过ClassLoader获取URL列表
        Enumeration<URL> en = getContextClassLoader().getResources(path);
        while (en.hasMoreElements()) {
            URL url = en.nextElement();
            URI uri = url.toURI(); // 转为绝对路径
            // 处理末尾多余的/或\
            String uriStr = removeTrailingSlash(uriToString(uri));
            String uriBaseStr = uriStr.substring(0, uriStr.length() - basePackagePath.length());
            if (uriBaseStr.startsWith("file:")) { // 在目录中搜索
                uriBaseStr = uriBaseStr.substring(5);
            }
            if (uriStr.startsWith("jar:")) { // 在jar包中搜索
                scanFile(true, uriBaseStr, jarUriToPath(basePackagePath, uri), collector, mapper);
            } else {
                scanFile(false, uriBaseStr, Paths.get(uri), collector, mapper);
            }
        }
    }

    /**
     * 获取ClassLoader
     * @return
     */
    ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        // 先从线程中获取ClassLoader
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) { // 获取不到就从当前Class获取
            cl = getClass().getClassLoader();
        }
        return cl;
    }

    /**
     * 将jar包的Url转化为Path
     * @param basePackagePath
     * @param jarUri
     * @return
     * @throws IOException
     */
    Path jarUriToPath(String basePackagePath, URI jarUri) throws IOException {
        return FileSystems.newFileSystem(jarUri, Map.of()).getPath(basePackagePath);
    }

    /**
     * 扫描文件
     * @param isJar
     * @param base
     * @param root
     * @param collector
     * @param mapper
     * @param <R>
     * @throws IOException
     */
    <R> void scanFile(boolean isJar, String base, Path root, List<R> collector, Function<Resource, R> mapper) throws IOException {
        String baseDir = removeTrailingSlash(base);
        Files.walk(root).filter(Files::isRegularFile).forEach(file -> {
            Resource res = null;
            if (isJar) { // 处理jar
                // 构建Resource
                res = new Resource(baseDir, removeLeadingSlash(file.toString()));
            } else { // 处理file
                String path = file.toString();
                String name = removeLeadingSlash(path.substring(baseDir.length()));
                res = new Resource("file:" + path, name);
            }
            logger.atDebug().log("found resource: {}", res);
            R r = mapper.apply(res);
            if (r != null) {
                collector.add(r); // add扫描到的文件
            }
        });
    }

    /**
     * 将URI转换为UTF-8的String
     * @param uri
     * @return
     */
    String uriToString(URI uri) {
        return URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
    }


    /**
     * Windows和Linux/macOS的路径分隔符不同，前者是\，后者是/，需要正确处理；
     * 移除开头的/或\
     * @param s
     * @return
     */
    String removeLeadingSlash(String s) {
        if (s.startsWith("/") || s.startsWith("\\")) {
            s = s.substring(1);
        }
        return s;
    }

    /**
     * 扫描目录时，返回的路径可能是abc/xyz，也可能是abc/xyz/，需要注意处理末尾的/
     * @param s
     * @return
     */
    String removeTrailingSlash(String s) {
        if (s.endsWith("/") || s.endsWith("\\")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
